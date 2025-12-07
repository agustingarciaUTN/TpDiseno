package Facultad.TrabajoPracticoDesarrollo.Controllers;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoUsuario;
import Facultad.TrabajoPracticoDesarrollo.Services.GestorUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*") // Permitir acceso desde cualquier frontend
public class UsuarioController {

    private final GestorUsuario gestorUsuario;

    // Inyección de Dependencias del Servicio (Gestor)
    @Autowired
    public UsuarioController(GestorUsuario gestorUsuario) {
        this.gestorUsuario = gestorUsuario;
    }

    /**
     * Endpoint para validar credenciales (Login)
     * Recibe un JSON con: { "nombre": "...", "contrasenia": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DtoUsuario loginRequest) {
        try {
            // Validamos que vengan los datos mínimos
            if (loginRequest.getNombre() == null || loginRequest.getContrasenia() == null) {
                return ResponseEntity.badRequest().body("Faltan datos: nombre y contraseña son obligatorios.");
            }

            // Llamamos al servicio para autenticar
            boolean autenticado = gestorUsuario.autenticarUsuario(
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