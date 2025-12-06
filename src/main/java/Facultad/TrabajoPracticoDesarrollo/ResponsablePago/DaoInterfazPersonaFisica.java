package Facultad.TrabajoPracticoDesarrollo.ResponsablePago;
import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaFisica;
import Excepciones.PersistenciaException;

public interface DaoInterfazPersonaFisica {
    boolean persistirPersonaFisica(PersonaFisica persona) throws PersistenciaException;
    DtoPersonaFisica obtenerPorId(int id);
    // Modificar, Eliminar...
}