package Facultad.TrabajoPracticoDesarrollo.Factura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.NotaDeCredito;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;

public interface DaoInterfazNotaDeCredito {
    boolean persistirNota(NotaDeCredito nota) throws PersistenciaException;
    DtoNotaDeCredito obtenerPorNumero(String numero);
}