package Factura;
import Dominio.Factura;
import Excepciones.PersistenciaException;
import java.util.ArrayList;

public interface DaoInterfazFactura {
    boolean persistirFactura(Factura factura) throws PersistenciaException;
    boolean modificarFactura(Factura factura) throws PersistenciaException;
    boolean eliminarFactura(int id);
    Factura obtenerFacturaPorId(int id);
    ArrayList<Factura> obtenerTodas();

    /*
    ArrayList<Factura> obtenerPorEstadia(int idEstadia);
    ArrayList<Factura> obtenerImpagas();
    */
}