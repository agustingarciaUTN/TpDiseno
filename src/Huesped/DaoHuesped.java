package Huesped;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import Dominio.Huesped;
import Excepciones.PersistenciaException;
import Usuario.DtoUsuario;
import BaseDedatos.Coneccion;
import enums.TipoDocumento;
import enums.PosIva;

public class DaoHuesped implements DaoHuespedInterfaz {

    public boolean crearHuesped(DtoHuesped dto) throws PersistenciaException {

        // Asume que la columna en tu BD se llama 'id_direccion' preguntar como se llama bien
        String sql = "INSERT INTO huesped (nombres, apellido, telefono, tipo_documento, numero_documento, " +
        "cuit, pos_iva, fecha_nacimiento, ocupacion, nacionalidad, id_direccion) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?::\"Pos_IVA\", ?, ?, ?, ?)";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getNombres());
            pstmt.setString(2, dto.getApellido());
            pstmt.setLong(3, dto.getTelefono());
            pstmt.setString(4, dto.getTipoDocumento().name()); // Guardamos el Enum como String
            pstmt.setString(5, dto.getDocumento());
            pstmt.setString(6, dto.getCuit());
            pstmt.setString(7, dto.getPosicionIva().toString());

            // Convertir java.util.Date (del DTO) a java.sql.Date (para JDBC)
            pstmt.setDate(8, new java.sql.Date(dto.getFechaNacimiento().getTime()));

            pstmt.setString(9, dto.getOcupacion());
            pstmt.setString(10, dto.getNacionalidad());


            // Obtenemos el ID de la dirección (que ya se creó y seteó en el DTO)
            pstmt.setInt(11, dto.getDireccion().getId());

            int filasAfectadas = pstmt.executeUpdate();

            // Devuelve true si se insertó al menos 1 fila
            return filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistenciaException("Error al intentar crear el huésped en la BD", e);
        }
    }

    public boolean crearEmailHuesped(DtoHuesped dto) {
        String sql = "INSERT INTO email_huesped (tipo_documento, nro_documento, email) VALUES (?, ?, ?)";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dto.getTipoDocumento().name());
            pstmt.setString(2, dto.getDocumento());
            pstmt.setString(3, dto.getEmail());

            int filasAfectadas = pstmt.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al crear email del huésped: " + e.getMessage());
            return false;
        }
    }
    public DtoHuesped buscarPorTipoYNumeroDocumento(TipoDocumento tipoDoc, String numDoc) throws PersistenciaException {
        // Devuelve un DTO si lo encuentra, o null si no existe
        String sql = "SELECT * FROM huesped WHERE tipo_documento = ? AND numero_documento = ?";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Usamos .name() para convertir el Enum a String
            pstmt.setString(1, tipoDoc.name());
            pstmt.setString(2, numDoc);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    //encuentra un duplicado, guardamos los datos principales del existente para mostrarlos
                    //ESTO NO ESTABA EN EL CU9, PERO ME PARECE UN BUEN ANIADIDO
                    DtoHuesped h = new DtoHuesped();
                    h.setNombres(rs.getString("nombres"));
                    h.setApellido(rs.getString("apellido"));
                    h.setTipoDocumento(TipoDocumento.valueOf(rs.getString("tipo_documento")));
                    h.setDocumento(rs.getString("numero_documento"));

                    return h;
                }
            }
        } catch (SQLException e) {
            //por si hay error en la coneccion con la db
            throw new PersistenciaException("Error al buscar huésped por documento", e);
        }
        return null; // No encontró duplicado
    }
    
    public ArrayList<DtoHuesped> obtenerTodosLosHuespedes (){
       
        ArrayList<DtoHuesped> huespedesEncontrados = new ArrayList<>();

       String sql = "SELECT h.apellido, h.nombres, h.tipo_documento, h.numero_documento, h.telefono, h.cuit, h.nacionalidad, " +
                   "h.fecha_nacimiento, h.id_direccion, h.pos_iva, h.ocupacion, MAX(e.email) as email " +
                   "FROM huesped h " +
                   "LEFT JOIN email_huesped e ON h.tipo_documento = e.tipo_documento AND h.numero_documento = e.nro_documento " +
                   "GROUP BY h.tipo_documento, h.numero_documento, h.apellido, h.nombres, h.telefono, h.cuit, h.nacionalidad, h.fecha_nacimiento, h.id_direccion, h.pos_iva, h.ocupacion";
        try (Connection conn = Coneccion.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql);) {
        //Crear los recursos para la conexion y las interacciones con la base de datos
        // aca hace que Java invoque .close() automaticamente para cerrarlos en caso de error
        // o que termine el metodo y te ahorra un bloque finally
      
            while (rs.next()) {
                // Para cada fila, creamos un nuevo DTO
                DtoHuesped huespedDTO = new DtoHuesped();
                
                //Mapeamos cada columna al atributo correspondiente del DTO
                huespedDTO.setApellido(rs.getString("apellido"));
                huespedDTO.setNombres(rs.getString("nombres"));
                huespedDTO.setDocumento(rs.getString("numero_documento"));
                huespedDTO.setTelefono(rs.getLong("telefono"));
                huespedDTO.setCuit(rs.getString("cuit"));
                huespedDTO.setNacionalidad(rs.getString("nacionalidad"));
                huespedDTO.setOcupacion(rs.getString("ocupacion"));
                huespedDTO.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                huespedDTO.setEmail(rs.getString("email"));

                //Si hay direccion asociada, seteamos el idDireccion en el DTO
                int idDir = rs.getInt("id_direccion");
                if(!rs.wasNull()){
                    huespedDTO.setIdDireccion(idDir);
                } else{ 
                    huespedDTO.setIdDireccion(0); //0 indica que no hay direccion asociada
                }
                //Para los enums
                //Primero leemos los strings y después los convertimos a enum
                String tipoDocumentoStr = rs.getString("tipo_documento");
                String posicionIvaStr = rs.getString("pos_iva");
                
                // Convertimos y establecemos la posición IVA usando el nuevo método fromString
                huespedDTO.setPosicionIva(posicionIvaStr);
                
                // Convertimos y establecemos el tipo de documento
                try {
                    if (tipoDocumentoStr != null) {
                        huespedDTO.setTipoDocumento(TipoDocumento.valueOf(tipoDocumentoStr.toUpperCase()));
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println("Valor de tipo_documento no válido en la BD: " + tipoDocumentoStr);
                }
                //Añadimos el DTO a nuestra lista
                huespedesEncontrados.add(huespedDTO);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener los huéspedes de la base de datos: " + e.getMessage());
        }

        //Devolvemos la lista (puede estar vacía, pero nunca será nula)
        return huespedesEncontrados;
    }
    
    public ArrayList<DtoHuesped> obtenerHuespedesPorCriterio(DtoHuesped criterios){
    
        ArrayList<DtoHuesped> huespedesEncontrados = new ArrayList<>();
        
       StringBuilder sql = new StringBuilder(
            "SELECT h.apellido, h.nombres, h.tipo_documento, h.numero_documento, h.telefono, h.cuit, h.nacionalidad, " +
            "h.fecha_nacimiento, h.id_direccion, h.pos_iva, h.ocupacion, MAX(e.email) as email " + // <-- MAX() añadido
            "FROM huesped h " +
            "LEFT JOIN email_huesped e ON h.tipo_documento = e.tipo_documento AND h.numero_documento = e.nro_documento " +
            "WHERE 1=1");
        
        //Creamos una lista para guardar los parámetros que realmente usaremos
        List<Object> params = new ArrayList<>();

        //Añadimos las condiciones a la consulta dinámicamente
        if (criterios.getApellido() != null && !criterios.getApellido().trim().isEmpty()) {
            sql.append(" AND apellido LIKE ?");
            // Usamos LIKE para buscar apellidos que "empiecen con" el criterio
            params.add(criterios.getApellido() + "%"); 
        }
        if (criterios.getNombres() != null && !criterios.getNombres().trim().isEmpty()) {
            sql.append(" AND nombres LIKE ?");
            params.add(criterios.getNombres() + "%");
        }
       if (criterios.getTipoDocumento() != null) {
            sql.append(" AND h.tipo_documento = ?"); // <-- Se agregó h.
            params.add(criterios.getTipoDocumento().name()); 
        }
        if (!criterios.getDocumento().equals("0")) {
            sql.append(" AND h.numero_documento = ?");
            params.add(criterios.getDocumento());
        }

        //Ejecutamos la consulta con los parámetros recolectados
        sql.append(" GROUP BY h.tipo_documento, h.numero_documento, h.apellido, h.nombres, h.telefono, h.cuit, h.nacionalidad, h.fecha_nacimiento, h.id_direccion, h.pos_iva, h.ocupacion");
        try (Connection conn = Coneccion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());) {

            // Asignamos los parámetros con sus tipos correctos
           for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Long) {
                    pstmt.setLong(i + 1, (Long) param);
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                //Mapeamos los resultados a DTOs
                 while (rs.next()) {
                // Para cada fila, creamos un nuevo DTO
                DtoHuesped huespedDTO = new DtoHuesped();
                
                //Mapeamos cada columna al atributo correspondiente del DTO
                huespedDTO.setApellido(rs.getString("apellido"));
                huespedDTO.setNombres(rs.getString("nombres"));
                huespedDTO.setDocumento(rs.getString("numero_documento"));
                huespedDTO.setTelefono(rs.getLong("telefono"));
                huespedDTO.setCuit(rs.getString("cuit"));
                huespedDTO.setNacionalidad(rs.getString("nacionalidad"));
                huespedDTO.setOcupacion(rs.getString("ocupacion"));
                huespedDTO.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                huespedDTO.setEmail(rs.getString("email"));

                //Si hay direccion asociada, seteamos el idDireccion en el DTO
                int idDir = rs.getInt("id_direccion");
                if(!rs.wasNull()){
                    huespedDTO.setIdDireccion(idDir);
                } else{ 
                    huespedDTO.setIdDireccion(0); //0 indica que no hay direccion asociada
                }

                //Para el enum cambia
                //Primero leemos el tipo de documento como string y despues lo convertimos a enum
                String tipoDocumentoStr = rs.getString("tipo_documento");
                String posicionIvaStr = rs.getString("pos_iva");
                // Convertimos y establecemos la posición IVA usando el método fromString
                huespedDTO.setPosicionIva(posicionIvaStr);
                try {
                    if (tipoDocumentoStr != null) {
                        huespedDTO.setTipoDocumento(TipoDocumento.valueOf(tipoDocumentoStr.toUpperCase()));
                    }
                } catch (IllegalArgumentException e) {
                        System.err.println("Valor de tipo_documento no válido en la BD: " + tipoDocumentoStr);
                    }

                    //Añadimos el DTO a nuestra lista
                    huespedesEncontrados.add(huespedDTO);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar huéspedes por criterio: " + e.getMessage());
        }

        return huespedesEncontrados;
        
    }
public int obtenerIdDireccion(String tipoDocumento, String nroDocumento) {
        String sql = "SELECT id_direccion FROM huesped WHERE tipo_documento = ? AND numero_documento = ?";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipoDocumento);
            ps.setString(2, nroDocumento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_direccion");
                }
            }

            return -1;

        } catch (SQLException e) {
            System.err.println("Error al obtener ID de dirección: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Elimina un huésped de la base de datos (borrado físico)
     * @param tipoDocumento Tipo de documento del huésped
     * @param nroDocumento Número de documento del huésped
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarHuesped(String tipoDocumento, String nroDocumento) {
        String sql = "DELETE FROM huesped WHERE tipo_documento = ? AND numero_documento = ?";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipoDocumento);
            ps.setString(2, nroDocumento);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Huésped eliminado exitosamente");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar huésped: " + e.getMessage());
            return false;
        }
    }
    

    /**
     * Elimina una dirección de la base de datos
     * @param idDireccion ID de la dirección a eliminar
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarDireccion(int idDireccion) {
        if (idDireccion <= 0) {
            return false;
        }

        String sql = "DELETE FROM direccion WHERE id_direccion = ?";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDireccion);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Dirección eliminada exitosamente");
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error al eliminar dirección: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarEmailsHuesped(String tipoDocumento, String nroDocumento) {
        // Usamos nro_documento porque así se llama en email_huesped
        String sql = "DELETE FROM email_huesped WHERE tipo_documento = ? AND nro_documento = ?";

        try (Connection conn = Coneccion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tipoDocumento);
            ps.setString(2, nroDocumento);

            ps.executeUpdate();
            
            // Devolvemos true si la consulta se ejecutó (aunque haya borrado 0 filas)
            return true; 

        } catch (SQLException e) {
            System.err.println("Error al eliminar emails del huésped: " + e.getMessage());
            return false;
        }
    }
    public boolean docExistente(DtoHuesped criterios){
    
        ArrayList<DtoHuesped> huespedesEncontrados = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder("SELECT tipo_documento, numero_documento FROM huesped WHERE 1=1");
        
        //Creamos una lista para guardar los parámetros que realmente usaremos
        List<Object> params = new ArrayList<>();

        if (criterios.getTipoDocumento() != null) {
            sql.append(" AND tipo_documento = ?");
            params.add(criterios.getTipoDocumento().name()); //.name() es para devolver el valor del enum como string
        }
        if (!criterios.getDocumento().equals("0")) {
            sql.append(" AND numero_documento = ?");
            params.add(criterios.getDocumento());
        }

        //Ejecutamos la consulta con los parámetros recolectados
        try (Connection conn = Coneccion.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());) {

            // Asignamos los parámetros con sus tipos correctos
           for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    pstmt.setString(i + 1, (String) param);
                } else if (param instanceof Long) {
                    pstmt.setLong(i + 1, (Long) param);
                }
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                //Mapeamos los resultados a DTOs
                 while (rs.next()) {
                // Para cada fila, creamos un nuevo DTO
                DtoHuesped huespedDTO = new DtoHuesped();
                
                //Mapeamos cada columna al atributo correspondiente del DTO
                huespedDTO.setDocumento(rs.getString("numero_documento"));
                
                //Para el enum cambia
                //Primero leemos el tipo de documento como string y despues lo convertimos a enum
                String tipoDocumentoStr = rs.getString("tipo_documento");
                try {
                 if (tipoDocumentoStr != null) {
                 huespedDTO.setTipoDocumento(TipoDocumento.valueOf(tipoDocumentoStr.toUpperCase()));
                }
                } catch (IllegalArgumentException e) {
                  System.err.println("Valor de tipo_documento no válido en la BD: " + tipoDocumentoStr);
                }

                //Añadimos el DTO a nuestra lista
                huespedesEncontrados.add(huespedDTO);
            }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar huéspedes por criterio: " + e.getMessage());
        }

        if(huespedesEncontrados.size()>0){
            return true;
        } else {
            return false;
        }
    }



    public boolean modificarHuesped(DtoHuesped original, DtoHuesped modificado){
        Connection conn = null;
        try {
            conn = Coneccion.getConnection();
            conn.setAutoCommit(false); // Iniciamos una transacción

            // 1. Actualizar la tabla huesped
           String sqlHuesped = "UPDATE huesped SET apellido = ?, nombres = ?, tipo_documento = ?, numero_documento = ?, " +
                  "cuit = ?, pos_iva = ?::\"Pos_IVA\", fecha_nacimiento = ?, telefono = ?, ocupacion = ?, " +
                  "nacionalidad = ?, id_direccion = ? WHERE tipo_documento = ? AND numero_documento = ?";
                  
            try (PreparedStatement pstmt = conn.prepareStatement(sqlHuesped)) {
                pstmt.setString(1, modificado.getApellido());
                pstmt.setString(2, modificado.getNombres());
                pstmt.setString(3, modificado.getTipoDocumento() != null ? modificado.getTipoDocumento().name() : null);
                pstmt.setString(4, modificado.getDocumento());
                pstmt.setString(5, modificado.getCuit());
                pstmt.setString(6, modificado.getPosicionIva() != null ? modificado.getPosicionIva().toString() : null);

                if (modificado.getFechaNacimiento() != null) {
                    pstmt.setDate(7, new java.sql.Date(modificado.getFechaNacimiento().getTime()));
                } else {
                    pstmt.setNull(7, java.sql.Types.DATE);
                }

                pstmt.setLong(8, modificado.getTelefono());
                pstmt.setString(9, modificado.getOcupacion());
                pstmt.setString(10, modificado.getNacionalidad());

                if (modificado.getDireccion() != null) {
                    pstmt.setInt(11, modificado.getIdDireccion());
                } else {
                    pstmt.setNull(11, java.sql.Types.INTEGER);
                }

                // WHERE params: original tipo + numero
                pstmt.setString(12, original.getTipoDocumento() != null ? original.getTipoDocumento().name() : null);
                pstmt.setString(13, original.getDocumento());

                pstmt.executeUpdate();
            }

            // 2. Insertar el nuevo email en la tabla email_huesped
            // Si el huésped ya tiene ese email exacto, no hace nada (DO NOTHING).
            String sqlEmail = "INSERT INTO email_huesped (tipo_documento, nro_documento, email) VALUES (?, ?, ?) " +
                            "ON CONFLICT (tipo_documento, nro_documento, email) DO NOTHING";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sqlEmail)) {
                // Usamos .name() porque tipo_documento en la BD es un varchar/string
                pstmt.setString(1, modificado.getTipoDocumento().name()); 
                pstmt.setString(2, modificado.getDocumento());
                pstmt.setString(3, modificado.getEmail());
                
                pstmt.executeUpdate();
            }
            conn.commit(); // Confirmamos la transacción
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Si hay error, deshacemos los cambios
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al modificar huésped: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restauramos el autocommit
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
        }
    }
    /* Crear un nuevo huésped
    method CrearHuesped(DtoHuesped dto) -> boolean:
    iniciar conexión a la base de datos
    preparar consulta para insertar datos del huésped
    asignar valores de dto a la consulta
    ejecutar la consulta
    si la operación es exitosa:
    retornar true
    de lo contrario:
    retornar false

    // Modificar un huésped existente
    method ModificarHuesped(int idUsuario) -> boolean:
    iniciar conexión a la base de datos
    preparar consulta para actualizar datos del huésped con el idUsuario dado
    asignar valores actualizados a la consulta
    ejecutar la consulta
    si la operación es exitosa:
    retornar true
    de lo contrario:
    retornar false
    // Eliminar un huésped
    method EliminarHuesped(int idUsuario) -> boolean:
    iniciar conexión a la base de datos
    preparar consulta para eliminar el huésped con el idUsuario dado
    ejecutar la consulta
    si la operación es exitosa:
    retornar true
    de lo contrario:
    retornar false

    // Obtener un huésped por su ID
    method ObtenerHuesped(int idUsuario) -> Huesped:
    iniciar conexión a la base de datos
    preparar consulta para seleccionar datos del huésped con el idUsuario dado
    ejecutar la consulta
    si se encuentra un resultado:
    mapear los datos obtenidos a un objeto Huesped
    retornar el objeto Huesped
    de lo contrario:
    retornar null'''*/


//queremos tener diferentes metodos para devolver por ej una lista de dto?
//tenemos que controlar excepciones