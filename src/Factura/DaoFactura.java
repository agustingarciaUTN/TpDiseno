package Factura;

import BaseDedatos.Conexion;
import Dominio.Factura;
import Excepciones.PersistenciaException;
import java.sql.*;

public class DaoFactura implements DaoInterfazFactura {
    private static DaoFactura instancia;
    private DaoFactura() {}
    public static synchronized DaoFactura getInstance() {
        if (instancia == null) instancia = new DaoFactura();
        return instancia;
    }

    @Override
    public boolean persistirFactura(Factura factura) throws PersistenciaException {
        String sql = "INSERT INTO factura (numero_factura, fecha_emision, importe_total, id_estadia, id_nota_credito,id_responsable, importe_neto, fecha_vencimiento, \"IVA\", estado, tipo_factura) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, factura.getNumeroFactura());
            ps.setDate(2, new java.sql.Date(factura.getFechaEmision().getTime()));
            ps.setDouble(3, factura.getImporteTotal());
            ps.setInt(4, factura.getEstadia().getIdEstadia());
            if(factura.getNotaDeCredito() != null) ps.setString(5, factura.getNotaDeCredito().getNumeroNotaCredito());
            else ps.setNull(5, Types.INTEGER);
            ps.setInt(6, factura.getResponsable().getIdResponsablePago());
            ps.setDouble(7, factura.getImporteNeto());

            if (factura.getFechaVencimiento() != null) ps.setDate(8, new java.sql.Date(factura.getFechaVencimiento().getTime()));
            else ps.setNull(8, Types.DATE);

            ps.setDouble(9, factura.getIva());
            ps.setString(10, factura.getEstadoFactura().name());
            ps.setString(11, factura.getTipoFactura().name());

            int res = ps.executeUpdate();
            if (res > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) factura.setIdFactura(rs.getInt(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al persistir factura", e);
        }
    }

    /*// Implementar resto de métodos CRUD...
    @Override
    public boolean modificarFactura(Factura factura) throws PersistenciaException { return false; }
    @Override
    public boolean eliminarFactura(int id) { return false; }
    @Override
    public DtoFactura obtenerFacturaPorId(int id) { return null; }
    @Override
    public ArrayList<DtoFactura> obtenerTodas() { return new ArrayList<>(); }*/
}