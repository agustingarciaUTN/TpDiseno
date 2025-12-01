package Habitacion;

import BaseDedatos.Conexion;
import Dominio.Habitacion;
import enums.TipoHabitacion;
import enums.EstadoHabitacion;

import java.sql.*;

public class DaoHabitacion implements DaoInterfazHabitacion {
    private static DaoHabitacion instancia;
    private DaoHabitacion() {}
    public static synchronized DaoHabitacion getInstance() {
        if (instancia == null) instancia = new DaoHabitacion();
        return instancia;
    }

    @Override
    public Habitacion obtenerPorNumero(String numero) {
        String sql = "SELECT * FROM habitacion WHERE numero = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Habitacion h = new Habitacion.Builder(
                            rs.getString("numero"),
                            TipoHabitacion.valueOf(rs.getString("tipo_habitacion")),
                            rs.getInt("capacidad")
                    )
                            .estado(EstadoHabitacion.valueOf(rs.getString("estado_habitacion")))
                            .costo(rs.getFloat("costo_por_noche"))
                            .build();
                    return h;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }


    // Resto de métodos CRUD básicos (persistir, modificar, eliminar) ya siguen el patrón estándar
    @Override public boolean persistirHabitacion(Habitacion h) { return false; } // TODO
    @Override public boolean modificarHabitacion(Habitacion h) { return false; } // TODO
    @Override public boolean eliminarHabitacion(String numero) { return false; } // TODO
}