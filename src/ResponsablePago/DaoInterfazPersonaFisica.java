package ResponsablePago;
import Dominio.PersonaFisica;
import Excepciones.PersistenciaException;

public interface DaoInterfazPersonaFisica {
    boolean persistirPersonaFisica(PersonaFisica persona) throws PersistenciaException;
    DtoPersonaFisica obtenerPorId(int id);
    // Modificar, Eliminar...
}