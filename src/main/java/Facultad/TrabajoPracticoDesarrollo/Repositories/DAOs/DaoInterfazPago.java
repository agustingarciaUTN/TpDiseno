package Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Pago;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPago;


public interface DaoInterfazPago {
    boolean persistirPago(Pago pago) throws PersistenciaException;
    DtoPago obtenerPagoPorId(int id);
    // ArrayList<Pago> obtenerPorFactura(int idFactura);
}