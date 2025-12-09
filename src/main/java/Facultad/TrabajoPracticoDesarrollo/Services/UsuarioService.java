package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Usuario;
import Facultad.TrabajoPracticoDesarrollo.Repositories.UsuarioRepository;
import Facultad.TrabajoPracticoDesarrollo.Utils.UsuarioHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public boolean autenticarUsuario(String nombre, String contrasenia) {
        if (nombre == null || nombre.isBlank() || contrasenia == null || contrasenia.isBlank()) {
            return false;
        }

        // 1. Buscar usuario en BD
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombre(nombre);

        if (usuarioOpt.isEmpty()) {
            return false; // Usuario no existe
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Generar Hash de la contraseña ingresada
        String hashIngresado = UsuarioHelper.generarHashMD5(contrasenia);

        // 3. Comparar con el Hash guardado en la BD
        return hashIngresado.equals(usuario.getHashContrasenia());
    }
}