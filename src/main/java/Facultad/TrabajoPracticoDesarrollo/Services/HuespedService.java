package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuespedBusqueda;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.HuespedId;
import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.Repositories.*;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoHuesped;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearDireccion;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearHuesped;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service //Le avisa a Spring que esta clase contiene Lógica de Negocio y que debe estar disponible para ser inyectada en los Controllers
public class HuespedService {

    //Pedimos la interfaz del repositorio y Spring te da una implementación que funciona
    private final HuespedRepository huespedRepository;
    private final DireccionRepository direccionRepository;
    private final ReservaRepository reservaRepository;
    private final EstadiaRepository estadiaRepository;
    private final FacturaRepository facturaRepository;
    private final PersonaFisicaRepository personaFisicaRepository;

    @Autowired
    public HuespedService(
            HuespedRepository huespedRepository,
            DireccionRepository direccionRepository,
            ReservaRepository reservaRepository,
            EstadiaRepository estadiaRepository,
            FacturaRepository facturaRepository,
            PersonaFisicaRepository personaFisicaRepository) {
        this.huespedRepository = huespedRepository;
        this.direccionRepository = direccionRepository;
        this.reservaRepository = reservaRepository;
        this.estadiaRepository = estadiaRepository;
        this.facturaRepository = facturaRepository;
        this.personaFisicaRepository = personaFisicaRepository;
    }

    /**
     * Busca huéspedes según los criterios del DTO.
     * Si el DTO está vacío, devuelve todos.
     */
    @Transactional(readOnly = true)
    public List<Huesped> buscarHuespedes(DtoHuespedBusqueda criterios) {

        // 1. Decidimos qué metodo del Repository llamar
        if (criterios != null && !criterios.estanVacios()) {
            System.out.println("Buscando coincidencias...");

            // Convertir el enum a String para la query nativa
            String tipoStr = criterios.getTipoDocumento() != null ? 
                           criterios.getTipoDocumento().name() : null;

            // Usamos la query personalizada del Repository
            return huespedRepository.buscarPorCriterios(
                    criterios.getApellido(),
                    criterios.getNombres(),
                    tipoStr,
                    criterios.getNroDocumento()
            );
        } else {
            System.out.println("Sin filtro: Trayendo todos los huéspedes...");
            return huespedRepository.findAll();
        }
    }

    /**
     * Validaciones de negocio (adicionales a las anotaciones @Valid del DTO).
     * Ejemplo: Reglas cruzadas como CUIT vs IVA.
     */
    public List<String> validarDatosHuesped(DtoHuesped datos) {
        List<String> errores = new ArrayList<>();

        // Regla especial CUIT/IVA
        if (datos.getPosicionIva() == PosIva.RESPONSABLE_INSCRIPTO) {
            if (datos.getCuit() == null || datos.getCuit().trim().isEmpty()) {
                errores.add("El CUIT es obligatorio para Responsables Inscriptos.");
            }
        }
        return errores;
    }

    /**
     * Verifica si existe un huésped con esa clave compuesta.
     * Retorna la Entidad si existe, o null si no.
     */
    @Transactional(readOnly = true)
    public Huesped chequearDuplicado(DtoHuesped datos) {
        HuespedId id = new HuespedId(datos.getTipoDocumento(), datos.getNroDocumento());
        return huespedRepository.findById(id).orElse(null);
    }

    private Huesped crearSinPersistirHuesped(DtoHuesped dto) {
        return MapearHuesped.mapearDtoAEntidad(dto);
    }

    private Direccion crearSinPersistirDireccion(DtoDireccion dto) {
        return MapearDireccion.mapearDtoAEntidad(dto);
    }


    // Una vez que cargamos un objeto con findById, estamos obligados a trabajar sobre ESE objeto.
    // No podemos traer otro nuevo a partir del mapearDtoAEntidad, ya que tendria la misma PK.
    //a forma correcta y profesional en JPA no es "crear un objeto para pisar el viejo", sino "modificar el objeto que ya tenés en la mano"

    //Traés el objeto (findById). -> Está "Limpio".
    //
    //Le hacés setNombre(...), setTelefono(...). -> Ahora está "Sucio" (Dirty).
    //
    //Termina la transacción (@Transactional).
    //
    //Hibernate ve que está sucio y automáticamente genera el UPDATE solo con los campos que cambiaron.

    /**
     * Lógica de Alta o Modificación (UPSERT).
     * Reemplaza la lógica manual de JDBC por lógica JPA.
     */
    @Transactional
    public void upsertHuesped(DtoHuesped dto) {
        // 1. Buscamos si ya existe en la BD (Respetando el "alt" del diagrama)
        HuespedId id = new HuespedId(dto.getTipoDocumento(), dto.getNroDocumento());
        Optional<Huesped> existenteOpt = huespedRepository.findById(id);

        if (existenteOpt.isPresent()) {
            // ==========================================
            // CAMINO: MODIFICACIÓN (El huésped YA existe)
            // ==========================================
            Huesped huespedExistente = existenteOpt.get();

            // A. Actualizamos dirección manual (Respetando diagrama DD)
            // 1. Primero obtenemos la dirección REAL de la base de datos (que vive dentro del huésped)
            Direccion direccionExistente = huespedExistente.getDireccion();

            // 2. Verificamos que existan datos para actualizar
            if (direccionExistente != null && dto.getDtoDireccion() != null) {
                // Caso 1: Tenía dirección y la actualizamos
                MapearDireccion.actualizarEntidadDesdeDto(direccionExistente, dto.getDtoDireccion());
                direccionRepository.save(direccionExistente);

            } else if (direccionExistente == null && dto.getDtoDireccion() != null) {
                // Caso 2: No tenía dirección, pero ahora le cargaron una
                Direccion nuevaDir = crearSinPersistirDireccion(dto.getDtoDireccion());
                direccionRepository.save(nuevaDir); // Guardamos la nueva
                huespedExistente.setDireccion(nuevaDir); // Asociamos
            }

            // B. Actualizamos datos simples del Huésped
            MapearHuesped.actualizarEntidadDesdeDto(huespedExistente, dto);


            // C. Guardamos finalmente el Huésped
            // DIAGRAMA: "GHU -> DHU: modificarYPersistirHuesped"
            huespedRepository.save(huespedExistente);

        } else {
            // ==========================================
            // CAMINO: ALTA (El huésped NO existe)
            // ==========================================

            // 1. Instanciamos los objetos (sin ID todavía)

            // "GHU -> D: crearSinPersistirDireccion"
            Direccion nuevaDireccion = crearSinPersistirDireccion(dto.getDtoDireccion());

            // "GHU -> H: crearSinPersistirHuesped"
            Huesped nuevoHuesped = crearSinPersistirHuesped(dto);


            // 2. Guardamos la Dirección PRIMERO (Independiente)
            // Esto genera el ID en la base de datos para la dirección.
            // DIAGRAMA: "GHU -> DD: persistirDireccion"
            direccionRepository.save(nuevaDireccion);

            // 3. Asociamos la dirección persistida al huésped
            nuevoHuesped.setDireccion(nuevaDireccion);

            // 4. Guardamos el Huésped
            // DIAGRAMA: "GHU -> DHU: persistirHuesped"
            huespedRepository.save(nuevoHuesped);
        }


    }

    // CU10
    @Transactional
    public void modificarHuesped(String tipoOrig, String nroOrig, DtoHuesped dtoNuevo) {

        // 1. Validar existencia del original (El que estamos editando)
        HuespedId idOriginal = new HuespedId(TipoDocumento.valueOf(tipoOrig), nroOrig);
        Huesped huespedOriginal = huespedRepository.findById(idOriginal)
                .orElseThrow(() -> new RuntimeException("El huésped a modificar no existe."));

        // 2. Detectar si cambió la Identidad (PK)
        boolean cambioIdentidad = !tipoOrig.equals(dtoNuevo.getTipoDocumento().name()) ||
                !nroOrig.equals(dtoNuevo.getNroDocumento());

        if (cambioIdentidad) {
            // Verificar si el NUEVO DNI ya existe
            HuespedId idNuevo = new HuespedId(dtoNuevo.getTipoDocumento(), dtoNuevo.getNroDocumento());
            Optional<Huesped> huespedDestinoOpt = huespedRepository.findById(idNuevo);

            if (huespedDestinoOpt.isPresent()) {
                // === CASO "ACEPTAR IGUALMENTE" (Fusión) ===
                // El usuario confirmó usar un DNI que ya existía.

                Huesped huespedDestino = huespedDestinoOpt.get();

                // A. Pasamos los datos del formulario al huésped destino (el que sobrevive)
                MapearHuesped.actualizarEntidadDesdeDto(huespedDestino, dtoNuevo);
                asignarDireccionSegura(huespedDestino, dtoNuevo.getDtoDireccion());

                // B. Guardamos al destino actualizado
                huespedRepository.save(huespedDestino);

                // --- C. MIGRACIÓN DE HISTORIAL ---

                // 1. Reservas y Estadías (Apuntan directa e indirectamente a Huesped)
                reservaRepository.migrarReservas(
                        TipoDocumento.valueOf(tipoOrig), nroOrig, // Buscamos las del viejo
                        huespedDestino.getTipoDocumento(), huespedDestino.getNroDocumento(), // Ponemos ID nuevo
                        huespedDestino.getNombres(), huespedDestino.getApellido(), // Ponemos Nombre nuevo
                        huespedDestino.getTelefono().isEmpty() ? null : String.valueOf(huespedDestino.getTelefono().get(0))
                );

                // TODO: migrarEstadias necesita ser reimplementada (Estadia tiene List<Huesped>)
                // estadiaRepository.migrarEstadias(huespedOriginal, huespedDestino);

                // 2. FACTURAS
                // Buscamos si el huésped original tenía un rol de Pagador (Persona Fisica)
                Optional<PersonaFisica> pfOriginalOpt = personaFisicaRepository.findByHuesped(huespedOriginal);

                if (pfOriginalOpt.isPresent()) {
                    PersonaFisica pfOriginal = pfOriginalOpt.get();

                    // Buscamos si el destino YA es pagador
                    Optional<PersonaFisica> pfDestinoOpt = personaFisicaRepository.findByHuesped(huespedDestino);

                    if (pfDestinoOpt.isPresent()) {
                        // CASO A: Ambos eran pagadores.
                        // Hay que mover las facturas del PF viejo al PF nuevo y borrar el PF viejo.
                        PersonaFisica pfDestino = pfDestinoOpt.get();
                        facturaRepository.migrarFacturas(pfOriginal, pfDestino);

                        personaFisicaRepository.flush();

                        personaFisicaRepository.delete(pfOriginal); // Borramos el rol de pagador viejo
                    } else {
                        // CASO B: El nuevo no era pagador.
                        // Simplemente le cambiamos el dueño a la Persona Física existente.
                        pfOriginal.setHuesped(huespedDestino);
                        personaFisicaRepository.save(pfOriginal);
                    }
                }

                reservaRepository.flush();
                estadiaRepository.flush();
                facturaRepository.flush();

                // Al final, borramos al original y limpiamos SU dirección vieja
                Direccion dirOriginal = huespedOriginal.getDireccion();
                huespedRepository.delete(huespedOriginal);
                huespedRepository.flush();
                limpiarDireccionHuerfana(dirOriginal);

                return; // Terminamos acá la fusión

            } else {
                // === CASO CAMBIO DE DNI LIMPIO (Migración) ===
                // El nuevo DNI está libre. Hacemos el UPDATE de ID mágico.

                huespedRepository.actualizarIdentidad(
                        TipoDocumento.valueOf(tipoOrig), nroOrig,
                        dtoNuevo.getTipoDocumento(), dtoNuevo.getNroDocumento()
                );
                huespedRepository.flush();

                // Actualizamos las variables para que el paso 3 apunte al nuevo ID
                tipoOrig = dtoNuevo.getTipoDocumento().name();
                nroOrig = dtoNuevo.getNroDocumento();
            }
        }

        // 3. Actualización normal (Si no hubo fusión)
        // Buscamos el objeto (sea el mismo ID viejo o el nuevo ID si migramos)
        Huesped huesped = huespedRepository.findById(new HuespedId(TipoDocumento.valueOf(tipoOrig), nroOrig)).get();

        MapearHuesped.actualizarEntidadDesdeDto(huesped, dtoNuevo);
        asignarDireccionSegura(huesped, dtoNuevo.getDtoDireccion());

        huespedRepository.save(huesped);
    }

    // Helper para dirección

    /**
     * Actualiza la dirección de un huésped de forma segura.
     * Si la dirección actual es compartida por otros, crea una nueva para no afectar a terceros.
     * Si la dirección es exclusiva, la actualiza in-situ.
     */
    private void asignarDireccionSegura(Huesped huesped, DtoDireccion dtoNuevosDatos) {
        if (dtoNuevosDatos == null) return; // O manejar borrado si aplica

        Direccion direccionActual = huesped.getDireccion();

        if (direccionActual == null) {
            // Caso 1: No tenía dirección. Creamos una nueva.
            Direccion nueva = crearSinPersistirDireccion(dtoNuevosDatos);
            direccionRepository.save(nueva);
            huesped.setDireccion(nueva);
        } else {
            // Caso 2: Ya tiene dirección. Verificamos si es compartida.
            // Contamos cuántos la usan (incluyéndolo a él mismo, así que mínimo da 1)
            long cuantosLaUsan = huespedRepository.countByDireccion(direccionActual);

            if (cuantosLaUsan > 1) {
                // === CASO COMPARTIDO (Copy-on-Write) ===
                // Viven otros aquí. NO tocamos la original.

                // 1. Creamos una nueva entidad con los datos nuevos
                Direccion nuevaCasa = crearSinPersistirDireccion(dtoNuevosDatos);
                direccionRepository.save(nuevaCasa);

                // 2. Mudamos al huésped
                huesped.setDireccion(nuevaCasa);

                // (La dirección vieja queda intacta para los otros)

            } else {
                // === CASO EXCLUSIVO (In-Place Update) ===
                // Solo él vive acá. Podemos reciclar el objeto.
                MapearDireccion.actualizarEntidadDesdeDto(direccionActual, dtoNuevosDatos);
                direccionRepository.save(direccionActual);
            }
        }
    }

    @Transactional
    public void darDeBajaHuesped(String tipo, String nro) {

        HuespedId id = new HuespedId(TipoDocumento.valueOf(tipo), nro);
        Huesped huesped = huespedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El huésped a eliminar no existe."));

        // FILTRO 1: ¿Se alojó? (Regla del PDF)
        if (estadiaRepository.existsByHuespedesContaining(huesped)) {
            throw new RuntimeException("El huésped no puede ser eliminado pues se ha alojado en el Hotel.");
        }

        // FILTRO 2: ¿Tiene Facturas? (Regla Fiscal Implícita)
        // Buscamos si actúa como Persona Física
        Optional<PersonaFisica> pfOpt = personaFisicaRepository.findByHuesped(huesped);

        if (pfOpt.isPresent()) {
            // Este huésped tiene un rol de Pagador activo en el sistema
            if (facturaRepository.existsByResponsablePago(pfOpt.get())) {
                // Si es un pagador asociado a una factura
                throw new RuntimeException("El huésped no puede ser eliminado porque tiene facturas asociadas.");
            }

            // Si pasó el filtro, borramos el rol de pagador (persona fisica)
            personaFisicaRepository.delete(pfOpt.get());
        }

        // Guardamos la referencia a la dirección antes de borrar al dueño
        Direccion direccionABorrar = huesped.getDireccion();

        // 2. Limpieza de reservas
        reservaRepository.deleteByHuesped(huesped.getTipoDocumento(), huesped.getNroDocumento());

        huespedRepository.delete(huesped);

        // 3. FLUSH (Vital)
        // Necesitamos que el delete del huésped impacte en la BD para que el count baje.
        huespedRepository.flush();

        // 4. Limpieza de Dirección (Recolector de Basura)
        limpiarDireccionHuerfana(direccionABorrar);


    }

    // En HuespedService.java

    /**
     * Intenta borrar una dirección SOLO si nadie más la está usando.
     * Se debe llamar DESPUÉS de haber eliminado/desvinculado al huésped.
     */
    private void limpiarDireccionHuerfana(Direccion direccion) {
        if (direccion != null) {
            // Verificamos si quedó alguien más usándola
            long cantidadUsos = huespedRepository.countByDireccion(direccion);

            if (cantidadUsos == 0) {
                // Nadie la usa, es basura. La borramos.
                direccionRepository.delete(direccion);
            }
        }
    }
}