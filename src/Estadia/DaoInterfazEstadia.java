package Estadia;

import Dominio.Estadia;
import Dominio.Huesped;
import Excepciones.PersistenciaException;


import java.util.ArrayList;
import java.util.List;

public interface DaoInterfazEstadia {

    boolean modificarEstadia(DtoEstadia dto);
    boolean eliminarEstadia(int idEstadia);
    DtoEstadia obtenerEstadiaPorId(int idEstadia);
    ArrayList<DtoEstadia> obtenerTodasLasEstadias();
    boolean persistirEstadia(Estadia estadia, List<Huesped> huespedes) throws PersistenciaException;

    // Método específico para validar si un huésped tiene estadías (CU11)
    boolean huespedTieneEstadias(String tipoDocumento, String nroDocumento);
}