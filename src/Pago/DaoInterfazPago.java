package Pago;
import Dominio.Pago;
import Excepciones.PersistenciaException;
import java.util.ArrayList;

public interface DaoInterfazPago {
    boolean persistirPago(Pago pago) throws PersistenciaException;
    Pago obtenerPagoPorId(int id);
    // ArrayList<Pago> obtenerPorFactura(int idFactura);
}