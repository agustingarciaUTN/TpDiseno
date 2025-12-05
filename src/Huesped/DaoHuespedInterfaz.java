package Huesped;

import Dominio.Huesped;
import Excepciones.PersistenciaException;
import enums.TipoDocumento;
import java.util.ArrayList;

public interface DaoHuespedInterfaz {

    // Persistencia (Recibe Entidad completa y valida)
    boolean persistirHuesped(Huesped huesped) throws PersistenciaException;
    boolean modificarHuesped(Huesped huesped) throws PersistenciaException;
    boolean eliminarHuesped(TipoDocumento tipo, String nroDocumento);


    // Búsquedas (Devuelven Entidades completas)
    DtoHuesped obtenerHuesped(TipoDocumento tipo, String nroDocumento);
    ArrayList<DtoHuesped> obtenerTodosLosHuespedes();

    // Búsqueda por Criterio (Recibe filtros sueltos para no forzar una Entidad inválida)
    ArrayList<DtoHuesped> obtenerHuespedesPorCriterio(DtoHuesped criterios);

    boolean existeHuesped(TipoDocumento tipo, String nroDocumento);

    // Métodos auxiliares necesarios
    int obtenerIdDireccion(TipoDocumento tipo, String nroDocumento);


    }