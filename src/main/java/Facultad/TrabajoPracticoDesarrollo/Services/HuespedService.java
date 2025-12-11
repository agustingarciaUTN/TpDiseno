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

            // Usamos la query personalizada del Repository
            return huespedRepository.buscarPorCriterios(
                    criterios.getApellido(),
                    criterios.getNombres(),
                    criterios.getTipoDocumento(),
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
                actualizarDireccionExistente(huespedDestino, dtoNuevo.getDtoDireccion());

                // B. Guardamos al destino actualizado
                huespedRepository.save(huespedDestino);

                // --- C. MIGRACIÓN DE HISTORIAL ---

                // 1. Reservas y Estadías (Apuntan directo a Huesped)
                reservaRepository.migrarReservas(huespedOriginal, huespedDestino);
                estadiaRepository.migrarEstadias(huespedOriginal, huespedDestino);

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

                        facturaRepository.flush();

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

                huespedRepository.delete(huespedOriginal);

                return; // Terminamos acá.

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
        actualizarDireccionExistente(huesped, dtoNuevo.getDtoDireccion());

        huespedRepository.save(huesped);
    }

    // Helper para dirección (si no lo tenías ya)
    private void actualizarDireccionExistente(Huesped huesped, DtoDireccion dtoDir) {
        if (huesped.getDireccion() != null && dtoDir != null) {
            MapearDireccion.actualizarEntidadDesdeDto(huesped.getDireccion(), dtoDir);
            direccionRepository.save(huesped.getDireccion());
        } else if (huesped.getDireccion() == null && dtoDir != null) {
            Direccion nueva = crearSinPersistirDireccion(dtoDir);
            direccionRepository.save(nueva);
            huesped.setDireccion(nueva);
        }
    }

}