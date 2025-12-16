package Facultad.pruebas.Services;

import Facultad.TrabajoPracticoDesarrollo.DTOs.DtoUsuario;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Usuario;
import Facultad.TrabajoPracticoDesarrollo.Repositories.UsuarioRepository;
import Facultad.TrabajoPracticoDesarrollo.Services.UsuarioService;
import Facultad.TrabajoPracticoDesarrollo.Utils.UsuarioHelper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void autenticarUsuario_CredencialesValidas_RetornaTrue() {
        // Arrange
        String nombreUsuario = "admin";
        String passwordPlana = "1234";

        // Calculamos el hash esperado (simulamos lo que hay en la BD)
        String hashEsperado = UsuarioHelper.generarHashMD5(passwordPlana);

        // 1. Crear Entidad Mockeada (lo que devuelve la BD)
        Usuario usuarioReal = new Usuario.Builder()
                .id(1)
                .user(nombreUsuario)
                .password(hashEsperado) // La BD tiene el hash, no la plana
                .build();

        // 2. Mockear el repositorio: Buscamos por NOMBRE (no por ID ni por 'user')
        when(usuarioRepository.findByNombre(nombreUsuario)).thenReturn(Optional.of(usuarioReal));

        // Act
        // El servicio buscará por nombre y luego hasheará la pass plana para comparar
        boolean resultado = usuarioService.autenticarUsuario(nombreUsuario, passwordPlana);

        // Assert
        assertTrue(resultado, "El login debería ser exitoso con credenciales correctas");
        verify(usuarioRepository).findByNombre(nombreUsuario);
    }

    @Test
    void autenticarUsuario_UsuarioNoExiste_RetornaFalse() {
        // Arrange
        String nombreUsuario = "fantasma";
        when(usuarioRepository.findByNombre(nombreUsuario)).thenReturn(Optional.empty());

        // Act
        boolean resultado = usuarioService.autenticarUsuario(nombreUsuario, "1234");

        // Assert
        assertFalse(resultado, "El login debería fallar si el usuario no existe");
    }

    @Test
    void autenticarUsuario_ContraseniaIncorrecta_RetornaFalse() {
        // Arrange
        String nombreUsuario = "admin";
        String passCorrecta = "claveDificil";
        String hashCorrecto = UsuarioHelper.generarHashMD5(passCorrecta);

        Usuario usuarioReal = new Usuario.Builder()
                .id(1)
                .user(nombreUsuario)
                .password(hashCorrecto)
                .build();

        when(usuarioRepository.findByNombre(nombreUsuario)).thenReturn(Optional.of(usuarioReal));

        // Act
        // Intentamos entrar con una clave incorrecta
        boolean resultado = usuarioService.autenticarUsuario(nombreUsuario, "claveIncorrecta");

        // Assert
        assertFalse(resultado, "El login debería fallar si la contraseña no coincide");
    }
}