package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Usuario;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Spring crea la consulta automáticamente buscando por el campo 'nombre'
    Optional<Usuario> findByNombre(String nombre);

    @NotNull Optional<Usuario> findById(@NotNull Integer id);
}