package ResponsablePago;
import Dominio.PersonaJuridica;
import Excepciones.PersistenciaException;

public interface DaoInterfazPersonaJuridica {
    boolean persistirPersonaJuridica(PersonaJuridica pj) throws PersistenciaException;
    boolean modificarPersonaJuridica(PersonaJuridica pj) throws PersistenciaException;
    boolean eliminarPersonaJuridica(int id);
    PersonaJuridica obtenerPorId(int id);
    // PersonaJuridica obtenerPorCuit(String cuit);
}