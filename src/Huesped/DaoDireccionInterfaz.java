package Huesped;

import Excepciones.PersistenciaException;

public interface DaoDireccionInterfaz {
    DtoDireccion crearDireccion(DtoDireccion dto) throws PersistenciaException;
    boolean modificarDireccion(DtoDireccion dto) throws PersistenciaException;
    boolean eliminarDireccion(int idDireccion);
    DtoDireccion obtenerDireccion(int idDireccion);
}
//queremos tener diferentes metodos para devolver por ej una lista de dto?
//tenemos que controlar excepciones