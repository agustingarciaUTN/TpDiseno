package MedioDePago;
import Dominio.MedioPago;
import Excepciones.PersistenciaException;
import java.sql.Connection;
import java.sql.SQLException;

public interface DaoInterfazMedioDePago {
    // se usa desde DaoPago
    void persistirMedioPagoTransaccional(MedioPago mp, int idPago, Connection conn) throws SQLException;
}