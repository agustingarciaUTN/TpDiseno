package Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs;


import Facultad.TrabajoPracticoDesarrollo.Dominio.MedioPago;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Pago;
import Facultad.TrabajoPracticoDesarrollo.Excepciones.PersistenciaException;
import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoPago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class DaoPago implements DaoInterfazPago {
    private final DaoInterfazMedioDePago daoInterfazMedioDePago;

    @Autowired
    public DaoPago(DaoInterfazMedioDePago daoInterfazMedioDePago) {
        this.daoInterfazMedioDePago = daoInterfazMedioDePago;
    }


    @Override
    public boolean persistirPago(Pago pago) throws PersistenciaException {
        String sql = "INSERT INTO pago (id_factura, fecha_pago, monto_total, cotizacion, moneda) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false);

            int idPago;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, pago.getFactura().getIdFactura());
                ps.setDate(2, new Date(pago.getFechaPago().getTime()));
                ps.setDouble(3, pago.getMontoTotal());
                ps.setDouble(4, pago.getCotizacion());
                ps.setString(5, pago.getMoneda().name());
                ps.executeUpdate();
                try(ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) idPago = rs.getInt(1);
                    else throw new SQLException("Fallo ID Pago");
                }
                pago.setIdPago(idPago);
            }

            // Guardar Medios de Pago
            if (pago.getMediosPago() != null) {
                for (MedioPago mp : pago.getMediosPago()) {
                    this.daoInterfazMedioDePago.persistirMedioPagoTransaccional(mp, idPago, conn);
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if(conn != null) try { conn.rollback(); } catch(SQLException ignored){}
            throw new PersistenciaException("Error persistir Pago", e);
        } finally {
            if(conn != null) try { conn.setAutoCommit(true); conn.close(); } catch(SQLException ignored){}
        }
    }

    @Override
    public DtoPago obtenerPagoPorId(int id) {
        // Implementar con Builder y cargar medios de pago
        return null; // TODO
    }
}