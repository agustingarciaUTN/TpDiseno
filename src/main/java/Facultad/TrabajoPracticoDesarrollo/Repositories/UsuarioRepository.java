package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Usuario;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio Spring Data JPA para la entidad {@link Usuario}.
 *
 * <p>Provee operaciones CRUD básicas heredadas de {@link JpaRepository}
 * y consultas específicas para la búsqueda por nombre e identificación.</p>
 *
 * <p>Las implementaciones de los métodos personalizados son generadas automáticamente
 * por Spring Data en tiempo de ejecución.</p>
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    /**
     * Busca un usuario por su nombre.
     *
     * <p>Spring Data genera la consulta correspondiente basándose en el nombre del método.
     * Se espera que el parámetro sea el valor exacto del campo {@code nombre} en la entidad.</p>
     *
     * @param nombre nombre del usuario a buscar
     * @return {@link Optional} que contiene el {@link Usuario} si existe, o vacío si no se encuentra
     */
    Optional<Usuario> findByNombre(String nombre);

    /**
     * Obtiene un usuario por su identificador.
     *
     * <p>Declarado aquí con anotaciones {@link NotNull} para reflejar la intención de no aceptar
     * valores nulos en la entrada y en el Optional devuelto (el Optional puede estar vacío pero no es nulo).</p>
     *
     * @param id identificador numérico del usuario (no nulo)
     * @return {@link Optional} con el {@link Usuario} si existe, o vacío si no se encontró
     */
    @NotNull Optional<Usuario> findById(@NotNull Integer id);
}