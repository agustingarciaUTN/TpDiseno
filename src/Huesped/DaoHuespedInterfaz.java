package Huesped;

import java.util.ArrayList;
import Dominio.Huesped;
import Excepciones.PersistenciaException;
import enums.TipoDocumento;

public interface DaoHuespedInterfaz {
    boolean crearHuesped(DtoHuesped dto) throws PersistenciaException;
    boolean modificarHuesped(DtoHuesped dtoHuespedOriginal, DtoHuesped dtoHuespedModificado);
    boolean eliminarHuesped(String tipoDocumento, String nroDocumento);
    ArrayList<DtoHuesped> obtenerTodosLosHuespedes ();
    ArrayList<DtoHuesped> obtenerHuespedesPorCriterio(DtoHuesped criterios);
    boolean docExistente(DtoHuesped criterios);
    int obtenerIdDireccion(String tipoDocumento, String nroDocumento);
    boolean eliminarDireccion(int idDireccion);
    boolean eliminarEmailsHuesped(String tipoDocumento, String nroDocumento);
    DtoHuesped buscarPorTipoYNumeroDocumento(TipoDocumento tipoDoc, String numDoc) throws PersistenciaException;
    boolean crearEmailHuesped(DtoHuesped dto) throws PersistenciaException;
}
//queremos tener diferentes metodos para devolver por ej una lista de dto?
//tenemos que controlar excepciones