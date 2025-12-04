package Reserva;

import Dominio.Reserva;
import Utils.Mapear.MapearReserva;
import enums.EstadoReserva;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GestorReserva {
    // 1. La única instancia (static y private)
    private static GestorReserva instancia;

    // Referencias a los DAO que este gestor necesita
    private final DaoInterfazReserva daoReserva;

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorReserva() desde fuera.
    private GestorReserva() {
        // Obtenemos las instancias del DAO
        this.daoReserva = DaoReserva.getInstance();
    }

    // 3. Metodo de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorReserva getInstance() {
        if (instancia == null) {
            instancia = new GestorReserva();
        }
        return instancia;
    }

    // Metodo para saber si existe una reserva en x fecha, usamos para mapear las reservas de una habitación.
    public boolean estaReservadaEnFecha(String nroHabitacion, java.util.Date fechaInicial, java.util.Date fechaFin) {
        return daoReserva.hayReservaEnFecha(nroHabitacion, fechaInicial, fechaFin);
    }
    public List<DtoReserva> buscarReservasEnFecha(Date inicio, Date fin) {
        return ((DaoReserva) daoReserva).obtenerReservasEnPeriodo(inicio, fin);
    }
    /**
     * Valida los datos de una lista de reservas antes de seguir.
     * Verifica campos obligatorios del responsable.
     */
    public List<String> validarDatosReserva(List<DtoReserva> reservas) {
        List<String> errores = new ArrayList<>();

        for (DtoReserva dto : reservas) {
            if (dto == null) {
                errores.add("No se han seleccionado habitaciones para reservar.");
                return errores;
            }

            if (dto.getNombreHuespedResponsable() == null || dto.getNombreHuespedResponsable().trim().isEmpty()) {
                errores.add("El nombre del responsable es obligatorio.");
            }
            if (dto.getApellidoHuespedResponsable() == null || dto.getApellidoHuespedResponsable().trim().isEmpty()) {
                errores.add("El apellido del responsable es obligatorio.");
            }
            if (dto.getTelefonoHuespedResponsable() == null || dto.getTelefonoHuespedResponsable().trim().isEmpty()) {
                errores.add("El teléfono del responsable es obligatorio.");
            }
        }
        return errores;
    }

    /**
     * CU04: Crear Reservas.
     * Recibe una lista de DTO (uno por habitación seleccionada), los convierte y persiste.
     */
    public void crearReservas(List<DtoReserva> listaDtos) throws Exception {

        // 1. Validamos (por seguridad, aunque la pantalla ya lo haya hecho)
        List<String> errores = validarDatosReserva(listaDtos);
        if (!errores.isEmpty()) {
            throw new Exception("Datos inválidos: " + String.join(", ", errores));
        }

        // 2. Loop para cada habitación seleccionada (Como indica el Diagrama)
        for (DtoReserva dto : listaDtos) {

            // GR -> R: crearSinPersistirReserva(dtoReserva) (Mapeo DTO -> Entidad)
            Reserva reservaEntidad = MapearReserva.mapearDtoAEntidad(dto);
            Date fechaReserva  = reservaEntidad.getFechaReserva();
            // Aseguramos estado inicial si no vino
            if(reservaEntidad.getEstadoReserva() == null) {
                reservaEntidad.setEstadoReserva(EstadoReserva.ACTIVA);
            }
            // La fecha de reserva es HOY
            reservaEntidad.setFechaReserva(new Date());

            // GR -> DR: persistirReserva(Reserva)
            boolean exito = daoReserva.persistirReserva(reservaEntidad);

            if (!exito) {
                // Manejo de rollback manual o log de error
                System.err.println("Error al persistir reserva para habitación: " + dto.getIdHabitacion());
                throw new Exception("Error al guardar la reserva en base de datos.");
            }
        }
    }
}
