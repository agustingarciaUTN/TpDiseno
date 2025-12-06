package Facultad.TrabajoPracticoDesarrollo.ResponsablePago;
import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;

public interface DaoInterfazPersonaFisica {
    boolean persistirPersonaFisica(PersonaFisica persona) throws PersistenciaException;
    DtoPersonaFisica obtenerPorId(int id);
    // Modificar, Eliminar...
}