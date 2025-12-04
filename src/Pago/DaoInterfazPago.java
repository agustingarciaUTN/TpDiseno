package Pago;
import Dominio.Pago;
import Excepciones.PersistenciaException;


public interface DaoInterfazPago {
    boolean persistirPago(Pago pago) throws PersistenciaException;
    DtoPago obtenerPagoPorId(int id);
    // ArrayList<Pago> obtenerPorFactura(int idFactura);
}