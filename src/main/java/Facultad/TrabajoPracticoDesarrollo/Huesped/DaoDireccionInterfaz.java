package Facultad.TrabajoPracticoDesarrollo.Huesped;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Excepciones.PersistenciaException;

public interface DaoDireccionInterfaz {
    boolean persistirDireccion(Direccion direccion) throws PersistenciaException;
    boolean modificarDireccion(Direccion direccion) throws PersistenciaException;
    boolean eliminarDireccion(int idDireccion);
    DtoDireccion obtenerDireccion(int idDireccion);

}