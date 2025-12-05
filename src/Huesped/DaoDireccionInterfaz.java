package Huesped;

import Dominio.Direccion;
import Excepciones.PersistenciaException;
import enums.TipoDocumento;

public interface DaoDireccionInterfaz {
    boolean persistirDireccion(Direccion direccion) throws PersistenciaException;
    boolean modificarDireccion(Direccion direccion) throws PersistenciaException;
    boolean eliminarDireccion(int idDireccion);
    DtoDireccion obtenerDireccion(int idDireccion);

}