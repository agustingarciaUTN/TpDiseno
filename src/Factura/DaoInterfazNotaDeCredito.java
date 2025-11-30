package Factura;
import Dominio.NotaDeCredito;
import Excepciones.PersistenciaException;

public interface DaoInterfazNotaDeCredito {
    boolean persistirNota(NotaDeCredito nota) throws PersistenciaException;
    NotaDeCredito obtenerPorNumero(String numero);
}