package Huesped;

import Excepciones.PersistenciaException;
import Dominio.Direccion;

public interface DaoDireccionInterfaz {
    DtoDireccion persistirDireccion(DtoDireccion dto) throws PersistenciaException;
    boolean modificarDireccion(DtoDireccion dto) throws PersistenciaException;
    boolean eliminarDireccion(int idDireccion);
    DtoDireccion obtenerDireccion(int idDireccion);

    public Direccion crearDireccion(DtoDireccion dto);
}
//queremos tener diferentes metodos para devolver por ej una lista de dto?
//tenemos que controlar excepciones