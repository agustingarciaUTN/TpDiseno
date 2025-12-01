package Huesped;

import Dominio.Direccion;
import Excepciones.PersistenciaException;

public interface DaoDireccionInterfaz {
    Direccion persistirDireccion(Direccion direccion) throws PersistenciaException;
    boolean modificarDireccion(Direccion direccion) throws PersistenciaException;
    boolean eliminarDireccion(int idDireccion);
    Direccion obtenerDireccion(int idDireccion);
}