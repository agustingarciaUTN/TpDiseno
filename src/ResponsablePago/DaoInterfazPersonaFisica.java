package ResponsablePago;
import Dominio.PersonaFisica;
import Excepciones.PersistenciaException;

public interface DaoInterfazPersonaFisica {
    boolean persistirPersonaFisica(PersonaFisica persona) throws PersistenciaException;
    PersonaFisica obtenerPorId(int id);
    // Modificar, Eliminar...
}