package Factura;

import BaseDedatos.Coneccion;
import Dominio.NotaDeCredito;
import Excepciones.PersistenciaException;
import java.sql.*;

public class DaoNotaDeCredito implements DaoInterfazNotaDeCredito {
    private static DaoNotaDeCredito instancia;
    private DaoNotaDeCredito() {}
    public static synchronized DaoNotaDeCredito getInstance() {
        if (instancia == null) instancia = new DaoNotaDeCredito();
        return instancia;
    }

    @Override
    public boolean persistirNota(NotaDeCredito nota) throws PersistenciaException {
        String sql = "INSERT INTO nota_credito (numero_nota, monto_devolucion) VALUES (?, ?)";
        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nota.getNumeroNotaCredito());
            ps.setDouble(2, nota.getMontoDevolucion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al persistir NC", e);
        }
    }

    @Override
    public NotaDeCredito obtenerPorNumero(String numero) {
        String sql = "SELECT * FROM nota_credito WHERE numero_nota = ?";
        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new NotaDeCredito.Builder(
                            rs.getString("numero_nota"),
                            rs.getDouble("monto_devolucion")
                    ).build();
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}