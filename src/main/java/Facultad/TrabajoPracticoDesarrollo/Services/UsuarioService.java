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

    @Transactional
    public void crearUsuario(String nombre, String contrasenia) {
        // Verificar si el usuario ya existe
        Optional<Usuario> existente = usuarioRepository.findByNombre(nombre);
        if (existente.isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // Crear nuevo usuario con hash MD5
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUser(nombre);
        nuevoUsuario.setContrasenia(UsuarioHelper.generarHashMD5(contrasenia));
        
        usuarioRepository.save(nuevoUsuario);
    }

    @Transactional(readOnly = true)
    public boolean autenticarUsuario(String nombre, String contrasenia) {
        if (nombre == null || nombre.isBlank() || contrasenia == null || contrasenia.isBlank()) {
            return false;
        }

        // 1. Buscar usuario en BD
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombre(nombre);

        if (usuarioOpt.isEmpty()) {
            System.out.println("❌ Usuario no encontrado: " + nombre);
            return false; // Usuario no existe
        }

        Usuario usuario = usuarioOpt.get();

        // 2. Generar Hash de la contraseña ingresada
        String hashIngresado = UsuarioHelper.generarHashMD5(contrasenia);
        
        // DEBUG: Mostrar los hashes
        System.out.println("🔍 Hash ingresado: " + hashIngresado);
        System.out.println("🔍 Hash en BD: " + usuario.getContrasenia());

        // 3. Comparar con el Hash guardado en la BD
        boolean resultado = hashIngresado.equals(usuario.getContrasenia());
        System.out.println("🔍 ¿Coinciden? " + resultado);
        return resultado;
    }
}