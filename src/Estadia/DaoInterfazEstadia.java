package Estadia;

import java.util.ArrayList;

public interface DaoInterfazEstadia {
    boolean crearEstadia(DtoEstadia dto);
    boolean modificarEstadia(DtoEstadia dto);
    boolean eliminarEstadia(int idEstadia);
    DtoEstadia obtenerEstadiaPorId(int idEstadia);
    ArrayList<DtoEstadia> obtenerTodasLasEstadias();

    // Método específico para validar si un huésped tiene estadías (CU11)
    boolean huespedTieneEstadias(String tipoDocumento, String nroDocumento);
}