package Estadia;

import Dominio.Estadia;
import Dominio.Huesped;
import Excepciones.PersistenciaException;

import java.util.ArrayList;
import java.util.List;

public class GestorEstadia {
    private final DaoInterfazEstadia daoEstadia;

    public GestorEstadia(DaoInterfazEstadia dao) {
        this.daoEstadia = dao;
    }

    /**
     * Verifica si un huésped se ha alojado alguna vez en el hotel
     * @param tipoDocumento Tipo de documento del huésped
     * @param nroDocumento Número de documento del huésped
     * @return true si el huésped tiene al menos una estadía registrada
     */
    public boolean huespedSeAlojoAlgunaVez(String tipoDocumento, String nroDocumento) {
        if (tipoDocumento == null || tipoDocumento.trim().isEmpty()) {
            return false;
        }

        if (nroDocumento.isEmpty()) {
            return false;
        }

        return daoEstadia.huespedTieneEstadias(tipoDocumento, nroDocumento);
    }

    /**
     * Crea una nueva estadía
     * @param estadia Datos de la estadía
     * @param huespedes Lista de huespedes asociados
     * @return true si se creó exitosamente
     */
    public boolean crearEstadia(Estadia estadia, List<Huesped> huespedes) {
        if (estadia == null) {
            System.err.println("La estadía no puede ser nula");
            return false;
        }

        if (huespedes == null || huespedes.isEmpty()) {
            System.err.println("Debe haber al menos un huésped");
            return false;
        }
        if (estadia.getFechaInicio() == null) {
            System.err.println("La fecha de inicio es obligatoria");
            return false;
        }

        if (estadia.getValorEstadia() <= 0) {
            System.err.println("El valor de la estadía debe ser mayor a 0");
            return false;
        }

        try {
            return daoEstadia.crearEstadia(estadia, huespedes);
        } catch (PersistenciaException e) {
            System.err.println("Error al crear la estadía: " + e.getMessage());
            return false;
        }
    }

    /**
     * Modifica una estadía existente
     * @param dto Datos actualizados de la estadía
     * @return true si se modificó exitosamente
     */
    public boolean modificarEstadia(DtoEstadia dto) {
        if (dto == null) {
            System.err.println("Los datos de la estadía no pueden ser nulos");
            return false;
        }

        if (dto.getIdEstadia() <= 0) {
            System.err.println("El ID de la estadía no es válido");
            return false;
        }

        return daoEstadia.modificarEstadia(dto);
    }

    /**
     * Elimina una estadía
     * @param idEstadia ID de la estadía a eliminar
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarEstadia(int idEstadia) {
        if (idEstadia <= 0) {
            System.err.println("El ID de la estadía no es válido");
            return false;
        }

        return daoEstadia.eliminarEstadia(idEstadia);
    }

    /**
     * Obtiene una estadía por su ID
     * @param idEstadia ID de la estadía
     * @return DtoEstadia con los datos o null si no existe
     */
    public DtoEstadia obtenerEstadiaPorId(int idEstadia) {
        if (idEstadia <= 0) {
            return null;
        }

        return daoEstadia.obtenerEstadiaPorId(idEstadia);
    }

    /**
     * Obtiene todas las estadías
     * @return Lista de estadías
     */
    public ArrayList<DtoEstadia> obtenerTodasLasEstadias() {
        return daoEstadia.obtenerTodasLasEstadias();
    }
}