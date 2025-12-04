package Habitacion;

import BaseDedatos.Conexion;
import Dominio.Habitacion;
import enums.TipoHabitacion;
import enums.EstadoHabitacion;

import java.sql.*;
import java.util.ArrayList;

public class DaoHabitacion implements DaoInterfazHabitacion {
    private static DaoHabitacion instancia;
    private DaoHabitacion() {}
    public static synchronized DaoHabitacion getInstance() {
        if (instancia == null) instancia = new DaoHabitacion();
        return instancia;
    }

    @Override
    public DtoHabitacion obtenerPorNumero(String numero) {
        String sql = "SELECT * FROM habitacion WHERE numero = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DtoHabitacion h = new DtoHabitacion.Builder(
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


    @Override
    public ArrayList<DtoHabitacion> obtenerTodas(){
        String sql = "SELECT * FROM habitacion";
        ArrayList<DtoHabitacion> lista = new ArrayList<>();
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
            while (rs.next()) {
                lista.add(mapearHabitacion(conn, rs));
            }
        }catch (SQLException e) {e.printStackTrace();}
        return lista;
    }

    // PRIVATE
    private DtoHabitacion mapearHabitacion(Connection conn, ResultSet rs) throws SQLException {
        String numero = rs.getString("numero");
        String tipoStr = rs.getString("tipo_habitacion");
        TipoHabitacion tipo = tipoStr != null ? TipoHabitacion.fromString(tipoStr) : null;
        int capacidad = rs.getInt("capacidad");

        DtoHabitacion.Builder builder = new DtoHabitacion.Builder(numero, tipo, capacidad);

        String estadoStr = rs.getString("estado_habitacion");
        if (estadoStr != null) {
            builder.estado(EstadoHabitacion.fromString(estadoStr));
        }

        // getFloat devuelve 0.0 si es NULL; si quieres distinguir NULL usa rs.wasNull()
        float costo = rs.getFloat("costo_por_noche");
        if (!rs.wasNull()) {
            builder.costo(costo);
        }

        // Si hay que cargar reservas u otras relaciones, hacerlo aquí usando 'conn' y otros DAOs.
        return builder.build();
    }
    // Resto de métodos CRUD básicos (persistir, modificar, eliminar) ya siguen el patrón estándar
    @Override public boolean persistirHabitacion(Habitacion h) { return false; } // TODO
    @Override public boolean modificarHabitacion(Habitacion h) { return false; } // TODO
    @Override public boolean eliminarHabitacion(String numero) { return false; } // TODO
}