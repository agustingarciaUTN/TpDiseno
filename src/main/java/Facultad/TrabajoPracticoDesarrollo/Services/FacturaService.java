package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.*;
import Facultad.TrabajoPracticoDesarrollo.Dominio.*;
import Facultad.TrabajoPracticoDesarrollo.Repositories.*;
import Facultad.TrabajoPracticoDesarrollo.Utils.Mapear.MapearFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import Facultad.TrabajoPracticoDesarrollo.enums.PosIva;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoFactura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * El contador del sistema. Se encarga de calcular montos, impuestos (IVA)
 * y aplicar recargos si el huésped se porta mal (ej: sale tarde).
 */
@Service
public class FacturaService {

    private final EstadiaRepository estadiaRepository;
    private final FacturaRepository facturaRepository;
    private final ResponsablePagoRepository responsablePagoRepository;
    private final HuespedRepository huespedRepository;
    private final NotaDeCreditoRepository notaDeCreditoRepository;
    private final PersonaFisicaRepository personaFisicaRepository;
    private final ServiciosAdicionalesRepository serviciosAdicionalesRepository;
    private final DireccionRepository direccionRepository;

    @Autowired
    public FacturaService(DireccionRepository direccionRepository, ServiciosAdicionalesRepository serviciosAdicionalesRepository, PersonaFisicaRepository personaFisicaRepository, ResponsablePagoRepository responsablePagoRepository, HuespedRepository huespedRepository, EstadiaRepository estadiaRepository, FacturaRepository facturaRepository, NotaDeCreditoRepository notaDeCreditoRepository) {
        this.estadiaRepository = estadiaRepository;
        this.facturaRepository = facturaRepository;
        this.responsablePagoRepository = responsablePagoRepository;
        this.huespedRepository = huespedRepository;
        this.notaDeCreditoRepository = notaDeCreditoRepository;
        this.personaFisicaRepository = personaFisicaRepository;
        this.serviciosAdicionalesRepository = serviciosAdicionalesRepository;
        this.direccionRepository = direccionRepository;
    }

    // --- MÉTODOS DE BÚSQUEDA ---

    @Transactional(readOnly = true)
    public Factura buscarPorNumero(String numeroFactura) {
        return facturaRepository.findById(numeroFactura).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existeFactura(String numeroFactura) {
        return facturaRepository.existsById(numeroFactura);
    }


    // --- MÉTODOS TRANSACCIONALES ---

    /**
     * Guarda la factura físicamente en la BD.
     * Valida que no estemos duplicando números de comprobante.
     */
    @Transactional
    public void guardarFactura(Factura factura) throws Exception {
        if (factura == null) throw new IllegalArgumentException("La factura no puede ser nula.");

        // Validación simple: Si ya existe, no la sobreescribimos (salvo que sea lógica de update)
        if (existeFactura(factura.getNumeroFactura())) {
            throw new Exception("Ya existe una factura con el número: " + factura.getNumeroFactura());
        }

        // Si la factura tiene una Nota de Crédito asociada, JPA la guardará por cascada
        // si está configurado CascadeType.ALL en la entidad Factura.
        // Si no, deberíamos guardarla aquí:
        if (factura.getNotaDeCredito() != null) {
            notaDeCreditoRepository.save(factura.getNotaDeCredito());
        }

        facturaRepository.save(factura);
    }

    @Transactional
    public void actualizarEstado(String numeroFactura, EstadoFactura nuevoEstado) throws Exception {
        Factura factura = facturaRepository.findById(numeroFactura)
                .orElseThrow(() -> new Exception("No se encontró la factura " + numeroFactura));

        factura.setEstadoFactura(nuevoEstado);
        facturaRepository.save(factura);
    }

    @Transactional
    public void generarNotaDeCredito(NotaDeCredito nota) {
        // Lógica específica para notas de crédito
        notaDeCreditoRepository.save(nota);
    }

    /**
     * Recupera quiénes están en la habitación para mostrarlos en la pantalla de facturación.
     * Sirve para que el recepcionista elija quién de todos va a pagar.
     */
    @Transactional(readOnly = true)
    public DtoOcupantesHabitacion buscarOcupantes(String nroHabitacion) {

        LocalDate hoy = LocalDate.now();

        Estadia estadia = estadiaRepository.findEstadiaFacturable(nroHabitacion, hoy)
                .orElseThrow(() -> new IllegalArgumentException("No hay estadía activa en la habitación " + nroHabitacion));

        //Mapeamos la lista usando el Builder de DtoDatosOcupantes
        List<DtoDatosOcupantes> listaOcupantes = estadia.getEstadiaHuespedes().stream()
                .map(eh -> new DtoDatosOcupantes.Builder()
                        .tipoDocumento(eh.getHuesped().getTipoDocumento())
                        .nroDocumento(eh.getHuesped().getNroDocumento())
                        .nombres(eh.getHuesped().getNombres())
                        .apellido(eh.getHuesped().getApellido())
                        .build()
                ).collect(Collectors.toList());

        // Construimos la respuesta mayor con su Builder
        return new DtoOcupantesHabitacion.Builder()
                .numeroHabitacion(nroHabitacion)
                .idEstadia(estadia.getIdEstadia())
                .ocupantes(listaOcupantes)
                .build();
    }

    /**
     * Valida que la persona elegida para pagar sea mayor de edad.
     * Un menor no puede ser responsable legal de una factura.
     */
    public void validarResponsable(TipoDocumento tipo, String nro) {

        // Creamos el ID compuesto para buscar
        HuespedId id = new HuespedId(tipo, nro);

        Huesped h = huespedRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Huésped no encontrado"));

        LocalDate nacimiento = convertirAFechaLocal(h.getFechaNacimiento());

        if (nacimiento == null) {
            throw new IllegalArgumentException("El huésped no tiene fecha de nacimiento registrada. Complétela antes de facturar.");
        }

        // Debug: Imprime esto en consola para ver qué está calculando
        System.out.println("Fecha Nac: " + nacimiento + " | Edad: " + Period.between(nacimiento, LocalDate.now()).getYears());

        if (Period.between(nacimiento, LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("La persona seleccionada es menor de edad.");
        }
    }

    /**
     * Prepara la cuenta antes de cerrarla (Calculadora).
     * Aquí está la lógica del negocio importante:
     * - Si te vas después de las 11:00, te cobra medio día.
     * - Si te vas después de las 18:00, te cobra el día entero.
     *
     * @param idEstadia         Qué estadía estamos cobrando.
     * @param idResponsable Quién va a poner la plata.
     * @param horaSalida        A qué hora entregó la llave (para calcular la multa).
     * @return Un objeto con todos los números calculados (subtotal, recargos, total).
     */
    @Transactional(readOnly = true)
    public DtoDetalleFacturacion calcularDetalle(int idEstadia, int idResponsable, String horaSalida) {
        Estadia estadia = estadiaRepository.findById(idEstadia).orElseThrow();
        ResponsablePago responsable = responsablePagoRepository.findById(idResponsable).orElseThrow();

        // 1. Cálculo de Recargos
        LocalTime hora = LocalTime.parse(horaSalida);
        double costoNoche = estadia.getHabitacion().getCostoPorNoche();
        double recargo = 0.0;
        String detalleRecargo = "Sin recargo";

        if (hora.isAfter(LocalTime.of(11, 0))) {
            if (hora.isBefore(LocalTime.of(18, 1))) {
                recargo = costoNoche * 0.5;
                detalleRecargo = "Late Check-out (50%)";
            } else {
                recargo = costoNoche;
                detalleRecargo = "Late Check-out (Día completo)";
            }
        }

        // 2. Determinar Tipo de Factura
        TipoFactura tipo = TipoFactura.B;
        if (responsable instanceof PersonaJuridica) {
            tipo = TipoFactura.A;
        } else if (responsable instanceof PersonaFisica pf) {
            if (pf.getHuesped().getPosicionIva() == PosIva.RESPONSABLE_INSCRIPTO) {
                tipo = TipoFactura.A;
            }
        }

        List<ServiciosAdicionales> serviciosEntidad = serviciosAdicionalesRepository.findByEstadia_IdEstadia(idEstadia);

        double totalServicios = 0.0;
        List<DtoServiciosAdicionales> listaServiciosDto = new ArrayList<>();

        // Iteramos la lista que nos devolvió el repositorio
        for (ServiciosAdicionales s : serviciosEntidad) {

            totalServicios += s.getValor(); // Asumiendo que tiene getPrecio() o getValor()

            DtoServiciosAdicionales dtoItem = new DtoServiciosAdicionales.Builder()
                    .descripcion(s.getDescripcion())
                    .valor(s.getValor())
                    .build(); // <--- Importante: construye el objeto al final

            listaServiciosDto.add(dtoItem);
        }
        // 4. Totales
        double subtotal = estadia.getValorEstadia() + recargo + totalServicios;
        double iva = subtotal * 0.21;
        double totalFinal = subtotal + iva;

        // 5. Resolver Datos Responsable
        String nombreResp;
        String cuitResp = null;
        if(responsable instanceof PersonaJuridica pj) {
            nombreResp = pj.getRazonSocial();
            cuitResp = pj.getCuit();
        } else {
            PersonaFisica pf = (PersonaFisica) responsable;
            nombreResp = pf.getHuesped().getApellido() + " " + pf.getHuesped().getNombres();
            cuitResp = pf.getHuesped().getCuit(); // Si tiene
        }

        // 6. Construir DTO Final con Builder
        return new DtoDetalleFacturacion.Builder()
                .idResponsable(idResponsable)
                .nombreResponsable(nombreResp)
                .cuitResponsable(cuitResp)
                .montoEstadiaBase(estadia.getValorEstadia())
                .recargoHorario(recargo)
                .detalleRecargo(detalleRecargo)
                .serviciosAdicionales(listaServiciosDto)
                .subtotal(subtotal)
                .montoIva(iva)
                .montoTotal(totalFinal)
                .tipoFactura(tipo)
                .build();
    }

    /**
     * Emite la factura definitiva y la guarda para siempre.
     * Una vez que esto corre, ya queda registrada la deuda en el sistema.
     * Cierra la estadía (Check-out) asignando la fecha de emisión como salida real.
     */
    @Transactional
    public DtoFactura generarFactura(DtoFactura dto) throws Exception {

        //Los dos métodos que siguen los hice en un principio pero leyendo el enunciado creeria que no van
        /*
        if (existeFactura(dto.getNumeroFactura())) {
            throw new IllegalArgumentException("El número de factura ya existe: " + dto.getNumeroFactura());
        }

        //Verificar si ya existe factura para esta estadía
        Integer idEstadia = dto.getIdEstadia().getIdEstadia();
        List<Factura> facturasExistentes = facturaRepository.findByEstadia_IdEstadia(idEstadia);

        if (!facturasExistentes.isEmpty()) {
            //Obtenemos el número de la primera factura para ser específicos en el error
            String nroExistente = facturasExistentes.get(0).getNumeroFactura();
            throw new IllegalArgumentException("Error: Esta estadía ya fue facturada previamente con el comprobante N° " + nroExistente);
        }
        */

        Estadia estadia = estadiaRepository.findById(dto.getIdEstadia().getIdEstadia()).orElseThrow();

        Integer idDelResponsable = dto.getIdResponsable().getIdResponsable();

        ResponsablePago responsable = responsablePagoRepository.findById(idDelResponsable)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el responsable ID: " + idDelResponsable));

        //Genera numero de factura
        String puntoVenta = "0005"; // Fijo
        Factura ultimaFactura = facturaRepository.findTopByOrderByNumeroFacturaDesc();

        String ultimoNumero = null;
        if (ultimaFactura != null) {
            ultimoNumero = ultimaFactura.getNumeroFactura();
        }

        int siguienteNumero = 1;

        if (ultimoNumero != null && !ultimoNumero.isEmpty()) {
            try {
                String parteNumerica = ultimoNumero.split("-")[1];
                siguienteNumero = Integer.parseInt(parteNumerica) + 1;
            } catch (Exception e) {
                System.err.println("Error al parsear último número: " + ultimoNumero);
            }
        }

        // Formato final: "0005-XXXXXXXX" (8 dígitos con ceros a la izquierda)
        String numeroGenerado = String.format("%s-%08d", puntoVenta, siguienteNumero);

        Factura factura = MapearFactura.mapearDtoAEntidad(dto, responsable, estadia);

        factura.setNumeroFactura(numeroGenerado);

        // --- ARREGLO 2: FORZAR ESTADO PENDIENTE ---
        // No importa si viene null, aquí nace la factura.
        factura.setEstadoFactura(EstadoFactura.PENDIENTE);

        // --- ARREGLO 3: CALCULAR NETO SI FALTA ---
        // Si el neto viene nulo o es 0, lo calculamos desde el total.
        if (factura.getImporteNeto() == null || factura.getImporteNeto() == 0) {
            if (factura.getTipoFactura() == TipoFactura.A) {
                // Si es A, el Total tiene IVA. Neto = Total / 1.21
                double neto = factura.getImporteTotal() / 1.21;
                factura.setImporteNeto(neto);
                factura.setIva(factura.getImporteTotal() - neto);
            } else {
                // Si es B, usualmente el Neto es igual al Total (o se desglosa internamente igual)
                // Para simplificar, asumimos Neto = Total / 1.21 también si queremos guardar el valor sin impuestos,
                // o Neto = Total si consideramos que B no discrimina.
                // Lo estándar contable: El neto siempre es la base imponible.
                factura.setImporteNeto(factura.getImporteTotal() / 1.21);
                factura.setIva(0.0); // En B no se muestra, pero existe contablemente. O lo dejas en 0.
            }
        }

        facturaRepository.save(factura);

        estadia.setFechaCheckOut(factura.getFechaEmision());
        estadiaRepository.save(estadia);

        dto.setNumeroFactura(numeroGenerado);

        return dto;
    }

    /**
     * "Asciende" a un Huésped a la categoría de Responsable de Pago (Persona Física).
     * Si ya era responsable, devuelve su ID. Si no, lo crea copiando su dirección.
     */
    @Transactional
    public int obtenerOAltaPersonaFisica(TipoDocumento tipo, String nro) {
        // 1. Buscamos si ya existe como Responsable (PersonaFisica)
        Optional<PersonaFisica> pfExistente = personaFisicaRepository
                .findByHuesped_TipoDocumentoAndHuesped_NroDocumento(tipo, nro);

        if (pfExistente.isPresent()) {
            return pfExistente.get().getIdResponsable();
        }

        // 2. Si NO existe, lo creamos automáticamente (Promoción de Huésped a Responsable)
        // Buscamos al Huésped (Debe existir sí o sí por el paso anterior del Controller)
        HuespedId idHuesped = new HuespedId(tipo, nro);
        Huesped huesped = huespedRepository.findById(idHuesped)
                .orElseThrow(() -> new IllegalArgumentException("El huésped seleccionado no existe en la BD."));

        // 3. Creamos la entidad PersonaFisica
        PersonaFisica nuevaPf = new PersonaFisica();
        nuevaPf.setHuesped(huesped);

        if (huesped.getDireccion() != null) {
            Direccion dirOriginal = huesped.getDireccion();
            Direccion nuevaDireccion = new Direccion();

            // Usamos los SETTERS que Lombok genera automáticamente (@Setter)
            nuevaDireccion.setCalle(dirOriginal.getCalle());
            nuevaDireccion.setNumero(dirOriginal.getNumero());
            nuevaDireccion.setDepartamento(dirOriginal.getDepartamento());
            nuevaDireccion.setPiso(dirOriginal.getPiso());
            nuevaDireccion.setLocalidad(dirOriginal.getLocalidad());
            nuevaDireccion.setProvincia(dirOriginal.getProvincia());
            nuevaDireccion.setPais(dirOriginal.getPais());
            nuevaDireccion.setCodPostal(dirOriginal.getCodPostal());
            // Asignamos la nueva dirección clonada a la Persona Física
            nuevaPf.setDireccion(nuevaDireccion);
        }

        personaFisicaRepository.save(nuevaPf);

        return nuevaPf.getIdResponsable();
    }

    /**
     * Da de alta una Empresa (Persona Jurídica) como pagador.
     */
    @Transactional
    public int guardarResponsableJuridico(DtoPersonaJuridica dto) {

        // 1. Validaciones de negocio (ej. si el CUIT ya existe)
        Integer idExistente = responsablePagoRepository.buscarIdPorCuit(dto.getCuit());
        if (idExistente != null) {
            throw new IllegalArgumentException("El CUIT " + dto.getCuit() + " ya está registrado en el sistema.");
        }

        // 2. Mapeo DTO -> Entidad (Dirección)
        // Como el DTO viene del front, extraemos sus datos para llenar la entidad
        Direccion direccionEntidad = new Direccion();

        // Usamos los getters de tu DtoDireccion
        if (dto.getDtoDireccion() != null) {
            DtoDireccion dirDto = dto.getDtoDireccion();
            direccionEntidad.setCalle(dirDto.getCalle());
            direccionEntidad.setNumero(dirDto.getNumero());
            direccionEntidad.setPiso(dirDto.getPiso());
            direccionEntidad.setDepartamento(dirDto.getDepartamento());
            direccionEntidad.setCodPostal(dirDto.getCodPostal());
            direccionEntidad.setLocalidad(dirDto.getLocalidad());
            direccionEntidad.setProvincia(dirDto.getProvincia());
            direccionEntidad.setPais(dirDto.getPais());

            direccionEntidad = direccionRepository.save(direccionEntidad);
        }

        // 3. Mapeo DTO -> Entidad (Persona Jurídica)
        PersonaJuridica nuevaEmpresa = new PersonaJuridica();
        nuevaEmpresa.setRazonSocial(dto.getRazonSocial());
        nuevaEmpresa.setCuit(dto.getCuit());

        // Asumiendo que guardas el primer teléfono
        if (dto.getTelefono() != null && !dto.getTelefono().isEmpty()) {
            nuevaEmpresa.setTelefonos(dto.getTelefono());
        }

        nuevaEmpresa.setDireccion(direccionEntidad);

        /*
        if (direccionEntidad != null) {
            nuevaEmpresa.setDireccion(direccionEntidad);
        }
        */

        // 4. Guardar
        responsablePagoRepository.save(nuevaEmpresa);

        return nuevaEmpresa.getIdResponsable();
    }

    /**
     * Da de alta una Empresa (Persona Jurídica) como pagador.
     */
    @Transactional(readOnly = true)
    public Integer buscarIdPorCuit(String cuit) {
        if (cuit == null || cuit.trim().isEmpty()) return null;

        // Llamada eficiente a la BD
        return responsablePagoRepository.buscarIdPorCuit(cuit);
    }

    // --- MÉTODOS PRIVADOS ---

    /**
     * Normalizador de fechas (Helper interno).
     * Java tiene muchas clases de fechas (java.util.Date, java.sql.Date, Timestamp).
     * Este método convierte lo que sea que venga de la BD a un LocalDate moderno y manejable.
     *
     * @param fecha La fecha en formato antiguo o desconocido.
     * @return La fecha en formato LocalDate (Java 8+).
     */
    private LocalDate convertirAFechaLocal(java.util.Date fecha) {
        if (fecha == null) return null;

        // Caso 1: Es java.sql.Date (Base de datos común)
        if (fecha instanceof java.sql.Date) {
            return ((java.sql.Date) fecha).toLocalDate();
        }

        // Caso 2: Es java.sql.Timestamp (PostgreSQL a veces devuelve esto)
        if (fecha instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) fecha).toLocalDateTime().toLocalDate();
        }

        // Caso 3: Es java.util.Date puro (Memoria)
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}