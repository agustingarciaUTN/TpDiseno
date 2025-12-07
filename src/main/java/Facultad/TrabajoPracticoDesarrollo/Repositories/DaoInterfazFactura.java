package Facultad.TrabajoPracticoDesarrollo.Repositories;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;

public interface DaoInterfazFactura {
    boolean persistirFactura(Factura factura) throws PersistenciaException;
    //boolean modificarFactura(Factura factura) throws PersistenciaException;
   // boolean eliminarFactura(int id);
    //DtoFactura obtenerFacturaPorId(int id);
    //ArrayList<DtoFactura> obtenerTodas();


    //ArrayList<Factura> obtenerPorEstadia(int idEstadia);
    //ArrayList<Factura> obtenerImpagas();

}