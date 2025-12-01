package Factura;

import BaseDedatos.Conexion;
import Dominio.NotaDeCredito;
import Excepciones.PersistenciaException;
import java.sql.*;
import java.util.ArrayList;

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
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nota.getNumeroNotaCredito());
            ps.setDouble(2, nota.getMontoDevolucion());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al persistir NC", e);
        }
    }

    @Override
    public DtoNotaDeCredito obtenerPorNumero(String numero) {
        String sqlNota = "SELECT * FROM nota_credito WHERE numero_nota = ?";
        String sqlFacturas = "SELECT id_factura FROM factura WHERE id_nota_credito = ?";

        try (Connection conn = Conexion.getConnection();
             PreparedStatement psNota = conn.prepareStatement(sqlNota)) {

            psNota.setString(1, numero);
            try (ResultSet rsNota = psNota.executeQuery()) {
                if (!rsNota.next()) return null;

                String numeroNota = rsNota.getString("numero_nota");
                double monto = rsNota.getDouble("monto_devolucion");

                ArrayList<Integer> idsFacturas = new ArrayList<>();
                // Segunda consulta para obtener los ids de factura asociados
                try (PreparedStatement psFact = conn.prepareStatement(sqlFacturas)) {
                    psFact.setString(1, numeroNota);
                    try (ResultSet rsFact = psFact.executeQuery()) {
                        while (rsFact.next()) {
                            idsFacturas.add(rsFact.getInt("id_factura"));
                        }
                    }
                }

                return new DtoNotaDeCredito.Builder()
                        .numero(numeroNota)
                        .monto(monto)
                        .idsFacturas(idsFacturas)
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}