package MedioDePago;

import BaseDedatos.Coneccion;
import Dominio.MedioPago;
import Dominio.Efectivo;
import Dominio.Cheque;
import Dominio.TarjetaDebito;
import Dominio.TarjetaCredito;
import java.sql.*;

public class DaoMedioDePago implements DaoInterfazMedioDePago {

    private static DaoMedioDePago instancia;
    private DaoMedioDePago() {}

    public static synchronized DaoMedioDePago getInstance() {
        if (instancia == null) {
            instancia = new DaoMedioDePago();
        }
        return instancia;
    }

    @Override
    public void persistirMedioPagoTransaccional(MedioPago mp, int idPago, Connection conn) throws SQLException {

        // Variables para las FKs (solo una se llenará)
        Integer idEfectivo = null;
        String numeroCheque = null;
        String numeroTarjeta = null;

        // 1. Persistir en la tabla específica según el tipo
        if (mp instanceof Efectivo) {
            idEfectivo = persistirEfectivo((Efectivo) mp, conn);
        }
        else if (mp instanceof Cheque) {
            numeroCheque = persistirCheque((Cheque) mp, conn);
        }
        else if (mp instanceof TarjetaDebito) {
            numeroTarjeta = persistirTarjetaDebito((TarjetaDebito) mp, conn);
        }
        else if (mp instanceof TarjetaCredito) {
            numeroTarjeta = persistirTarjetaCredito((TarjetaCredito) mp, conn);
        }

        // Insertar en tabla de vinculación medio de pago
        String sqlVinculo = "INSERT INTO medio_pago (id_pago, id_efectivo, numero_tarjeta, numero_cheque) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sqlVinculo)) {
            ps.setInt(1, idPago);

            // Seteamos FK Efectivo
            if (idEfectivo != null) ps.setInt(2, idEfectivo);
            else ps.setNull(2, Types.INTEGER);

            // Seteamos FK Tarjeta (Debito o Credito comparten columna o son exclusivas segun tu diseño, asumo columna 'numero_tarjeta')
            if (numeroTarjeta != null) ps.setString(3, numeroTarjeta);
            else ps.setNull(3, Types.VARCHAR);

            // Seteamos FK Cheque
            if (numeroCheque != null) ps.setString(4, numeroCheque);
            else ps.setNull(4, Types.VARCHAR);

            ps.executeUpdate();
        }
    }

    // Metodos privados: son asi porque no nos interesa que un gestor pueda utilizarlos.
    // Son metodos para uso interno en el DAO y si fueran public podrian generar inconsistencias con los datos.

    private int persistirEfectivo(Efectivo e, Connection conn) throws SQLException {
        // Efectivo tiene sus propios datos: monto, moneda, fecha
        String sql = "INSERT INTO efectivo (monto, moneda, fecha_pago) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, e.getMonto());
            ps.setString(2, e.getMoneda().name()); // Enum a Mayúsculas
            ps.setDate(3, new java.sql.Date(e.getFechaDePago().getTime()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1); // Retorna id_efectivo generado
                throw new SQLException("No se generó ID para Efectivo");
            }
        }
    }

    private String persistirCheque(Cheque c, Connection conn) throws SQLException {
        String sql = "INSERT INTO cheque (numero_cheque, banco, plaza, monto, fecha_cobro, fecha_pago) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNumeroCheque()); // PK manual
            ps.setString(2, c.getBanco());
            ps.setString(3, c.getPlaza());
            ps.setDouble(4, c.getMonto());
            ps.setDate(5, new java.sql.Date(c.getFechaCobro().getTime()));
            ps.setDate(6, new java.sql.Date(c.getFechaDePago().getTime()));

            ps.executeUpdate();
            return c.getNumeroCheque(); // Retorna la PK
        }
    }

    private String persistirTarjetaDebito(TarjetaDebito td, Connection conn) throws SQLException {
        String sql = "INSERT INTO tarjeta_debito (numero_tarjeta, banco, fecha_vencimiento, cod_seguridad, monto, moneda, fecha_pago, red_pago) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, td.getNumeroDeTarjeta()); // PK
            ps.setString(2, td.getBanco());
            ps.setDate(3, new java.sql.Date(td.getFechaVencimiento().getTime()));
            ps.setInt(4, td.getCodigoSeguridad());
            ps.setDouble(5, td.getMonto());
            ps.setString(6, td.getMoneda().name());
            ps.setDate(7, new java.sql.Date(td.getFechaDePago().getTime()));
            ps.setString(8, td.getRedDePago().name());

            ps.executeUpdate();
            return td.getNumeroDeTarjeta();
        }
    }

    private String persistirTarjetaCredito(TarjetaCredito tc, Connection conn) throws SQLException {
        // Igual que débito pero con cuotas
        String sql = "INSERT INTO tarjeta_credito (numero_tarjeta, banco, fecha_vencimiento, cod_seguridad, monto, moneda, fecha_pago, red_pago, cuotas) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tc.getNumeroDeTarjeta()); // PK
            ps.setString(2, tc.getBanco());
            ps.setDate(3, new java.sql.Date(tc.getFechaVencimiento().getTime()));
            ps.setInt(4, tc.getCodigoSeguridad());
            ps.setDouble(5, tc.getMonto());
            ps.setString(6, tc.getMoneda().name());
            ps.setDate(7, new java.sql.Date(tc.getFechaDePago().getTime()));
            ps.setString(8, tc.getRedDePago().name());
            ps.setInt(9, tc.getCuotasCantidad());

            ps.executeUpdate();
            return tc.getNumeroDeTarjeta();
        }
    }
}