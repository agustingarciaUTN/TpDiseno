package Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
 import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoDireccion;

public interface DaoDireccionInterfaz {
    boolean persistirDireccion(Direccion direccion) throws PersistenciaException;
    boolean modificarDireccion(Direccion direccion) throws PersistenciaException;
    boolean eliminarDireccion(int idDireccion);
    DtoDireccion obtenerDireccion(int idDireccion);

}