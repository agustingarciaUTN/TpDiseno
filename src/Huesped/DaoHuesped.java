package Huesped;

import BaseDedatos.Conexion;
import Dominio.Huesped;
import Excepciones.PersistenciaException;
import enums.PosIva;
import enums.TipoDocumento;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DaoHuesped implements DaoHuespedInterfaz {

    private static DaoHuesped instancia;
    private DaoHuesped() {}

    public static synchronized DaoHuesped getInstance() {
        if (instancia == null) instancia = new DaoHuesped();
        return instancia;
    }

    // --- PERSISTIR (CREATE) ---
    @Override
    public boolean persistirHuesped(Huesped huesped) throws PersistenciaException {
        String sqlHuesped = "INSERT INTO huesped (tipo_documento, numero_documento, apellido, nombres, fecha_nacimiento, nacionalidad, id_direccion, pos_iva, cuit) " +
                "VALUES (?::\"Tipo_Documento\", ?, ?, ?, ?, ?, ?, ?::\"Pos_IVA\", ?)";

        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // INICIO TRANSACCIÓN

            // 1. Insertar Huesped
            try (PreparedStatement ps = conn.prepareStatement(sqlHuesped)) {
                ps.setString(1, huesped.getTipoDocumento().name());
                ps.setString(2, huesped.getNroDocumento());
                ps.setString(3, huesped.getApellido());
                ps.setString(4, huesped.getNombres());
                ps.setDate(5, new java.sql.Date(huesped.getFechaNacimiento().getTime()));
                ps.setString(6, huesped.getNacionalidad());
                ps.setInt(7, huesped.getDireccion().getId()); // La dirección ya debe existir (se crea antes)
                ps.setString(8, huesped.getPosicionIva().toString());
                ps.setString(9, huesped.getCuit());
                ps.executeUpdate();
            }

            // 2. Insertar Tablas Satélite (Multivaluados)
            insertarSatelites(conn, huesped);

            conn.commit(); // FIN TRANSACCIÓN
            return true;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException _) {}
            throw new PersistenciaException("Error al persistir huésped", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException _) {}
        }
    }

    // --- MODIFICAR (UPDATE) ---
    @Override
    public boolean modificarHuesped(Huesped huesped) throws PersistenciaException {
        String sqlUpdate = "UPDATE huesped SET apellido=?, nombres=?, fecha_nacimiento=?, nacionalidad=?, id_direccion=?, " +
                "pos_iva=?::\"Pos_IVA\", cuit=? " +
                "WHERE tipo_documento=?::\"Tipo_Documento\" AND numero_documento=?";
        Connection conn = null;
        try {
            conn = Conexion.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, huesped.getApellido());
                ps.setString(2, huesped.getNombres());
                ps.setDate(3, new java.sql.Date(huesped.getFechaNacimiento().getTime()));
                ps.setString(4, huesped.getNacionalidad());
                ps.setInt(5, huesped.getDireccion().getId());
                ps.setString(6, huesped.getPosicionIva().toString());
                ps.setString(7, huesped.getCuit());
                // WHERE
                ps.setString(8, huesped.getTipoDocumento().name());
                ps.setString(9, huesped.getNroDocumento());
                ps.executeUpdate();
            }

            // Actualizar satélites: Borrar viejos e insertar nuevos (Estrategia simple y segura)
            borrarSatelites(conn, huesped.getTipoDocumento().name(), huesped.getNroDocumento());
            insertarSatelites(conn, huesped);

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException _) {}
            throw new PersistenciaException("Error al modificar huésped", e);
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException _) {}
        }
    }

    // --- BUSCAR POR CRITERIO ---
    @Override
    public ArrayList<DtoHuesped> obtenerHuespedesPorCriterio(DtoHuesped criterios) {
        ArrayList<DtoHuesped> lista = new ArrayList<>();

        // 1. QUERY ROBUSTA: Usamos la misma base que en 'obtenerTodos' para traer Email y Dirección
        StringBuilder sql = new StringBuilder(
                "SELECT h.apellido, h.nombres, h.tipo_documento, h.numero_documento, h.telefono, h.cuit, h.nacionalidad, " +
                        "h.fecha_nacimiento, h.id_direccion, h.pos_iva, h.ocupacion, MAX(e.email) as email " +
                        "FROM huesped h " +
                        "LEFT JOIN email_huesped e ON h.tipo_documento = e.tipo_documento AND h.numero_documento = e.nro_documento " +
                        "WHERE 1=1");

        List<Object> params = new ArrayList<>();

        // 2. FILTROS DINÁMICOS
        if (criterios.getApellido() != null && !criterios.getApellido().isEmpty()) {
            sql.append(" AND h.apellido ILIKE ?"); // ILIKE en Postgres ignora mayúsculas/minúsculas
            params.add(criterios.getApellido() + "%");
        }

        if (criterios.getNombres() != null && !criterios.getNombres().isEmpty()) {
            sql.append(" AND h.nombres ILIKE ?");
            params.add(criterios.getNombres() + "%");
        }

        if (criterios.getTipoDocumento() != null) {
            // Casteo explícito al ENUM de Postgres si es necesario
            sql.append(" AND h.tipo_documento = ?::\"Tipo_Documento\"");
            params.add(criterios.getTipoDocumento().name());
        }

        // Validación del Documento: Ignoramos si es nulo, vacío o "0"
        if (criterios.getNroDocumento() != null && !criterios.getNroDocumento().isEmpty() && !criterios.getNroDocumento().equals("0")) {
            sql.append(" AND h.numero_documento LIKE ?");
            params.add(criterios.getNroDocumento() + "%");
        }

        // Agrupar es obligatorio al usar MAX(email)
        sql.append(" GROUP BY h.tipo_documento, h.numero_documento, h.apellido, h.nombres, h.telefono, h.cuit, h.nacionalidad, h.fecha_nacimiento, h.id_direccion, h.pos_iva, h.ocupacion");

        // 3. EJECUCIÓN Y MAPEO
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            // Asignar parámetros dinámicamente
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DtoHuesped dto = new DtoHuesped();

                    // Mapeo manual (Igual que en obtenerTodos)
                    dto.setApellido(rs.getString("apellido"));
                    dto.setNombres(rs.getString("nombres"));
                    dto.setNroDocumento(rs.getString("numero_documento"));
                    dto.setTelefono(Collections.singletonList(rs.getLong("telefono")));
                    dto.setCuit(rs.getString("cuit"));
                    dto.setNacionalidad(rs.getString("nacionalidad"));
                    dto.setOcupacion(Collections.singletonList(rs.getString("ocupacion")));
                    dto.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                    dto.setEmail(Collections.singletonList(rs.getString("email")));

                    int idDir = rs.getInt("id_direccion");

                    if (!rs.wasNull() && idDir > 0) {
                        // 1. Creamos un DTO de dirección auxiliar solo para transportar el ID
                        DtoDireccion dtoDirTemp = new DtoDireccion();
                        dtoDirTemp.setId(idDir);

                        // 2. Se lo asignamos al huésped
                        dto.setDtoDireccion(dtoDirTemp);
                    }

                    // Enums
                    try {
                        dto.setPosicionIva(PosIva.valueOf(rs.getString("pos_iva")));
                        String tipoDocStr = rs.getString("tipo_documento");
                        if (tipoDocStr != null) {
                            dto.setTipoDocumento(TipoDocumento.valueOf(tipoDocStr.toUpperCase()));
                        }
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error mapeando enums: " + e.getMessage());
                    }

                    lista.add(dto);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error en la búsqueda: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    // --- OBTENER TODOS ---
    @Override
    public ArrayList<DtoHuesped> obtenerTodosLosHuespedes() {
        ArrayList<DtoHuesped> lista = new ArrayList<>();
        String sql = "SELECT * FROM huesped";
        try (Connection conn = Conexion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapearHuesped(conn, rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    // --- OBTENER INDIVIDUAL ---
    @Override
    public DtoHuesped obtenerHuesped(TipoDocumento tipo, String nroDocumento) {
        String sql = "SELECT * FROM huesped WHERE tipo_documento=?::\"Tipo_Documento\" AND numero_documento=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo.name());
            ps.setString(2, nroDocumento);
            try (ResultSet rs = ps.executeQuery()) {
                //Procesar el ResultSet
                if (rs.next()) {
                    return mapearHuesped(conn, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- ELIMINAR ---
    @Override
    public boolean eliminarHuesped(TipoDocumento tipo, String nroDocumento) {
        // Nota: Los satélites deberían borrarse por CASCADE en la BD, si no, hay que hacerlo manual aquí
        String sql = "DELETE FROM huesped WHERE tipo_documento=?::\"Tipo_Documento\" AND numero_documento=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo.name());
            ps.setString(2, nroDocumento);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- MÉTODOS AUXILIARES (SATÉLITES y MAPEO) ---

    private void insertarSatelites(Connection conn, Huesped h) throws SQLException {
        String tipo = h.getTipoDocumento().name();
        String nro = h.getNroDocumento();

        // Teléfonos
        if (h.getTelefono() != null && !h.getTelefono().isEmpty()) {
            String sql = "INSERT INTO telefono_huesped (tipo_documento, nro_documento, telefono) VALUES (?::\"Tipo_Documento\", ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Long t : h.getTelefono()) {
                    ps.setString(1, tipo);
                    ps.setString(2, nro);
                    ps.setLong(3, t);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }

        // Emails
        if (h.getEmail() != null && !h.getEmail().isEmpty()) {//verificamos que exista la lista

            String sql = "INSERT INTO email_huesped (tipo_documento, nro_documento, email) VALUES (?::\"Tipo_Documento\", ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                boolean hayDatos = false; // Bandera para saber si agregamos algo

                for (String e : h.getEmail()) {
                    // 2. VALIDACIÓN: Que el string individual no sea null ni vacío
                    if (e != null && !e.isBlank()) {
                        ps.setString(1, tipo);
                        ps.setString(2, nro);
                        ps.setString(3, e.trim());
                        ps.addBatch();
                        hayDatos = true;
                    }
                }

                // 3. Solo ejecutamos si realmente cargamos algún email válido
                if (hayDatos) {
                    ps.executeBatch();
                }
            }
        }
        // Ocupaciones
        if (h.getOcupacion() != null && !h.getOcupacion().isEmpty()) {
            String sql = "INSERT INTO ocupacion_huesped (tipo_documento, nro_documento, ocupacion) VALUES (?::\"Tipo_Documento\", ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (String o : h.getOcupacion()) {
                    ps.setString(1, tipo);
                    ps.setString(2, nro);
                    ps.setString(3, o);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
        }
    }

    private void borrarSatelites(Connection conn, String tipo, String nro) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM telefono_huesped WHERE tipo_documento='" + tipo + "'::\"Tipo_Documento\" AND nro_documento='" + nro + "'");
            st.executeUpdate("DELETE FROM email_huesped WHERE tipo_documento='" + tipo + "'::\"Tipo_Documento\" AND nro_documento='" + nro + "'");
            st.executeUpdate("DELETE FROM ocupacion_huesped WHERE tipo_documento='" + tipo + "'::\"Tipo_Documento\" AND nro_documento='" + nro + "'");
        }
    }

    private DtoHuesped mapearHuesped(Connection conn, ResultSet rs) throws SQLException {
        // 1. Datos Básicos
        String tipoStr = rs.getString("tipo_documento");
        String nroDoc = rs.getString("numero_documento");

        // 2. Cargar Listas Satélite
        List<Long> tels = new ArrayList<>();
        List<String> emails = new ArrayList<>();
        List<String> ocups = new ArrayList<>();

        // Ejemplo Teléfonos
        try (PreparedStatement ps = conn.prepareStatement("SELECT telefono FROM telefono_huesped WHERE tipo_documento=?::\"Tipo_Documento\" AND nro_documento=?")) {
            ps.setString(1, tipoStr);
            ps.setString(2, nroDoc);
            try (ResultSet rsSub = ps.executeQuery()) {
                while (rsSub.next()) tels.add(Long.parseLong(rsSub.getString("telefono")));
            }
        }
        // Ejemplo Emails
        try (PreparedStatement ps = conn.prepareStatement("SELECT email FROM email_huesped WHERE tipo_documento=?::\"Tipo_Documento\" AND nro_documento=?")) {
            ps.setString(1, tipoStr);
            ps.setString(2, nroDoc);
            try (ResultSet rsSub = ps.executeQuery()) {
                while (rsSub.next()) emails.add(rsSub.getString("email"));
            }
        }
        // Ejemplo Ocupaciones
        try (PreparedStatement ps = conn.prepareStatement("SELECT ocupacion FROM ocupacion_huesped WHERE tipo_documento=?::\"Tipo_Documento\" AND nro_documento=?")) {
            ps.setString(1, tipoStr);
            ps.setString(2, nroDoc);
            try (ResultSet rsSub = ps.executeQuery()) {
                while (rsSub.next()) ocups.add(rsSub.getString("ocupacion"));
            }
        }

        // 3. Cargar Dirección
        DtoDireccion dir = DaoDireccion.getInstance().obtenerDireccion(rs.getInt("id_direccion"));

        // 4. Preparar Enumerados de forma segura
        PosIva pIva = null;
        try {
            // AQUÍ ESTÁ EL CAMBIO CLAVE: Usamos fromString para normalizar "EXENTO" -> Exento
            String posIvaDb = rs.getString("pos_iva");
            if (posIvaDb != null) {
                pIva = PosIva.fromString(posIvaDb);
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo mapear PosIva: " + rs.getString("pos_iva"));
            // Dejamos pIva como null o asignamos un default si es crítico
        }

        TipoDocumento tDoc = null;
        try {
            tDoc = TipoDocumento.valueOf(tipoStr);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo mapear TipoDocumento: " + tipoStr);
        }

        // 5. Construir Objeto
        return new DtoHuesped.Builder()
                .nombres(rs.getString("nombres"))
                .apellido(rs.getString("apellido"))
                .tipoDocumento(tDoc)
                .documento(nroDoc)
                .fechaNacimiento(rs.getDate("fecha_nacimiento"))
                .nacionalidad(rs.getString("nacionalidad"))
                .cuit(rs.getString("cuit"))
                .posicionIva(pIva)
                .direccion(dir)
                .telefono(tels)
                .email(emails)
                .ocupacion(ocups)
                .build();
    }

    @Override
    public int obtenerIdDireccion(TipoDocumento tipo, String nroDocumento) {
        DtoHuesped h = obtenerHuesped(tipo, nroDocumento);
        if(h != null && h.getDtoDireccion() != null) return h.getDtoDireccion().getId();
        return -1;
    }


    //Verificamos si en la DB existe un Huesped con el mismo Tipo y Numero de documento que el ingresado por formulario CU9
    @Override
    public boolean existeHuesped(TipoDocumento tipo, String nroDocumento) {
        String sql = "SELECT 1 FROM huesped WHERE tipo_documento=?::\"Tipo_Documento\" AND numero_documento=?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipo.name());
            ps.setString(2, nroDocumento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { return false; }
    }




}