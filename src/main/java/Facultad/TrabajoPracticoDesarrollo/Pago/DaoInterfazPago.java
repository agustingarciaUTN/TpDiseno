package Facultad.TrabajoPracticoDesarrollo.Pago;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Pago;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;


public interface DaoInterfazPago {
    boolean persistirPago(Pago pago) throws PersistenciaException;
    DtoPago obtenerPagoPorId(int id);
    // ArrayList<Pago> obtenerPorFactura(int idFactura);
}