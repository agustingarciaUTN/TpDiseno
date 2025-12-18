package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Usuario;
import Facultad.TrabajoPracticoDesarrollo.Repositories.UsuarioRepository;
import Facultad.TrabajoPracticoDesarrollo.Services.UsuarioService;
import Facultad.TrabajoPracticoDesarrollo.Utils.UsuarioHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link UsuarioService}.
 * <p>
 * Cubre la lógica de negocio del Caso de Uso:
 * <ul>
 * <li><b>CU01:</b> Autenticar Usuario (Login, validación de hash y manejo de errores).</li>
 * </ul>
 * También verifica que la creación de usuarios encripte las contraseñas correctamente.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    // ============================================================================================
    // CREAR USUARIO
    // ============================================================================================

    /**
     * <b>Caso de Prueba: Creación Exitosa</b>
     * <p>
     * Verifica que si el usuario no existe, se guarde en la base de datos
     * y, MUY IMPORTANTE, que la contraseña guardada NO sea la plana, sino el Hash.
     */
    @Test
    @DisplayName("Crear Usuario - Éxito: Debe guardar el usuario con la contraseña hasheada")
    void crearUsuario_Exito() {
        // ARRANGE
        String username = "admin";
        String rawPassword = "1234"; // Contraseña plana

        // Simulamos que el usuario NO existe
        when(usuarioRepository.findByNombre(username)).thenReturn(Optional.empty());

        // ACT
        usuarioService.crearUsuario(username, rawPassword);

        // ASSERT
        // Capturamos el objeto que se intentó guardar para inspeccionarlo
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario usuarioGuardado = usuarioCaptor.getValue();

        assertEquals(username, usuarioGuardado.getNombre());
        // Verificamos que la contraseña NO sea "1234"
        assertNotEquals(rawPassword, usuarioGuardado.getContrasenia());
        // Verificamos que sea efectivamente el Hash MD5 esperado (Usamos el mismo helper)
        assertEquals(UsuarioHelper.generarHashMD5(rawPassword), usuarioGuardado.getContrasenia());
    }

    /**
     * <b>Caso de Prueba: Usuario Duplicado</b>
     * <p>
     * Verifica que no se puedan crear dos usuarios con el mismo nombre.
     */
    @Test
    @DisplayName("Crear Usuario - Fallo: Debe lanzar excepción si el nombre de usuario ya existe")
    void crearUsuario_Fallo_Duplicado() {
        // ARRANGE
        String username = "yaexiste";
        when(usuarioRepository.findByNombre(username)).thenReturn(Optional.of(new Usuario()));

        // ACT & ASSERT
        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.crearUsuario(username, "cualquierCosa");
        });

        assertEquals("El usuario ya existe", exception.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    // ============================================================================================
    // CU01: AUTENTICAR USUARIO (LOGIN)
    // ============================================================================================

    /**
     * <b>Caso de Prueba: Login Exitoso</b>
     * <p>
     * Verifica que el login devuelva TRUE cuando la contraseña ingresada (hasheada)
     * coincide con la almacenada en la base de datos.
     */
    @Test
    @DisplayName("CU01 - Éxito: Debe autenticar correctamente si las credenciales coinciden")
    void autenticarUsuario_Exito() {
        // ARRANGE
        String username = "recepcionista";
        String passwordInput = "secret123";

        // Preparamos el usuario que vendría de la BD (con la contraseña YA hasheada)
        Usuario usuarioEnBD = new Usuario();
        usuarioEnBD.setNombre(username);
        usuarioEnBD.setContrasenia(UsuarioHelper.generarHashMD5(passwordInput)); // Simulamos hash guardado

        when(usuarioRepository.findByNombre(username)).thenReturn(Optional.of(usuarioEnBD));

        // ACT
        boolean resultado = usuarioService.autenticarUsuario(username, passwordInput);

        // ASSERT
        assertTrue(resultado, "El login debería ser exitoso");
    }

    /**
     * <b>Caso de Prueba: Usuario No Encontrado</b>
     * <p>
     * Verifica que el login falle si el nombre de usuario no existe en la BD.
     */
    @Test
    @DisplayName("CU01 - Fallo: Debe rechazar el login si el usuario no existe")
    void autenticarUsuario_Fallo_UsuarioInexistente() {
        // ARRANGE
        String username = "fantasma";
        when(usuarioRepository.findByNombre(username)).thenReturn(Optional.empty());

        // ACT
        boolean resultado = usuarioService.autenticarUsuario(username, "1234");

        // ASSERT
        assertFalse(resultado, "El login debería fallar si el usuario no existe");
    }

    /**
     * <b>Caso de Prueba: Contraseña Incorrecta</b>
     * <p>
     * Verifica que el login falle si el usuario existe pero la contraseña no coincide.
     */
    @Test
    @DisplayName("CU01 - Fallo: Debe rechazar el login si la contraseña es incorrecta")
    void autenticarUsuario_Fallo_PasswordIncorrecto() {
        // ARRANGE
        String username = "admin";
        String passwordCorrecta = "passwordReal";
        String passwordIncorrecta = "passwordFalso";

        Usuario usuarioEnBD = new Usuario();
        usuarioEnBD.setNombre(username);
        usuarioEnBD.setContrasenia(UsuarioHelper.generarHashMD5(passwordCorrecta));

        when(usuarioRepository.findByNombre(username)).thenReturn(Optional.of(usuarioEnBD));

        // ACT
        boolean resultado = usuarioService.autenticarUsuario(username, passwordIncorrecta);

        // ASSERT
        assertFalse(resultado, "El login debería fallar por contraseña incorrecta");
    }

    /**
     * <b>Caso de Prueba: Validaciones de Input</b>
     * <p>
     * Verifica que el método maneje inputs nulos o vacíos sin lanzar excepciones inesperadas (NullPointerException).
     */
    @Test
    @DisplayName("CU01 - Edge Case: Debe manejar inputs vacíos o nulos retornando false")
    void autenticarUsuario_InputsInvalidos() {
        assertFalse(usuarioService.autenticarUsuario(null, "123"));
        assertFalse(usuarioService.autenticarUsuario("admin", null));
        assertFalse(usuarioService.autenticarUsuario("", ""));
        assertFalse(usuarioService.autenticarUsuario("   ", "123"));

        // Verifica que ni siquiera fue a buscar a la base de datos
        verifyNoInteractions(usuarioRepository);
    }
}