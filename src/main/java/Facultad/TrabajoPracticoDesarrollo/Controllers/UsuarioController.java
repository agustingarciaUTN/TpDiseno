package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoUsuario;
import Facultad.TrabajoPracticoDesarrollo.Services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



/**
 * Controlador REST para operaciones sobre usuarios.
 * <p>
 * Expone endpoints bajo la ruta {@code /api/usuarios} para crear usuarios
 * (temporalmente para testing) y para validar credenciales (login).
 * Se permite acceso desde cualquier origen mediante CORS.
 * </p>
 */
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permitir acceso desde cualquier frontend
public class UsuarioController {

    private final UsuarioService serviceUsuario;


    /**
     * Construye un controlador de usuarios con la inyección del servicio.
     *
     * @param serviceUsuario servicio que contiene la lógica de negocio para usuarios
     */
    @Autowired
    public UsuarioController(UsuarioService serviceUsuario) {
        this.serviceUsuario = serviceUsuario;
    }

    /**
     * Endpoint temporal para crear un nuevo usuario. Diseñado solo para pruebas.
     * <p>
     * Recibe un JSON con los campos mínimos: {@code nombre} y {@code contrasenia}.
     * Valida que ambos campos estén presentes antes de delegar la creación al servicio.
     * </p>
     *
     * @param usuarioRequest DTO con los datos del usuario a crear ({@code nombre}, {@code contrasenia})
     * @return ResponseEntity con:
     *         - {@code 200 OK} y mensaje de éxito si se crea el usuario,
     *         - {@code 400 Bad Request} si faltan datos,
     *         - {@code 500 Internal Server Error} en caso de error del servidor.
     */
    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestBody DtoUsuario usuarioRequest) {
        try {
            if (usuarioRequest.getNombre() == null || usuarioRequest.getContrasenia() == null) {
                return ResponseEntity.badRequest().body("Faltan datos: nombre y contraseña son obligatorios.");
            }

            serviceUsuario.crearUsuario(usuarioRequest.getNombre(), usuarioRequest.getContrasenia());
            return ResponseEntity.ok("✅ Usuario creado exitosamente: " + usuarioRequest.getNombre());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error en el servidor: " + e.getMessage());
        }
    }

    /**
     * Endpoint para validar credenciales (login).
     * <p>
     * Recibe un JSON con {@code nombre} y {@code contrasenia}, valida la presencia de ambos
     * y solicita al servicio la autenticación. Responde con códigos HTTP apropiados según el resultado.
     * </p>
     *
     * @param loginRequest DTO con las credenciales a validar ({@code nombre}, {@code contrasenia})
     * @return ResponseEntity con:
     *         - {@code 200 OK} y mensaje de bienvenida si las credenciales son correctas,
     *         - {@code 401 Unauthorized} si las credenciales son incorrectas,
     *         - {@code 400 Bad Request} si faltan datos,
     *         - {@code 500 Internal Server Error} en caso de error del servidor.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DtoUsuario loginRequest) {
        try {
            // Validamos que vengan los datos mínimos
            if (loginRequest.getNombre() == null || loginRequest.getContrasenia() == null) {
                return ResponseEntity.badRequest().body("Faltan datos: nombre y contraseña son obligatorios.");
            }

            // Llamamos al servicio para autenticar
            boolean autenticado = serviceUsuario.autenticarUsuario(
                    loginRequest.getNombre(),
                    loginRequest.getContrasenia()
            );

            if (autenticado) {
                // Login exitoso -> 200 OK
                return ResponseEntity.ok("✅ Login exitoso. Bienvenido " + loginRequest.getNombre());
            } else {
                // Fallo de credenciales -> 401 Unauthorized
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Usuario o contraseña incorrectos.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error en el servidor: " + e.getMessage());
        }
    }
}