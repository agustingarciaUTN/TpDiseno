package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link Habitacion}.
 *
 * <p>Proporciona operaciones CRUD y de paginación heredadas de {@link JpaRepository}
 * para la entidad {@code Habitacion} cuyo identificador es el número de habitación
 * de tipo {@code String}.</p>
 *
 * <p>Notas prácticas:
 * - No son necesarios métodos adicionales por ahora; {@link JpaRepository} ya
 *   expone métodos útiles como {@code findById(String)}, {@code findAll()},
 *   {@code save(...)} y {@code delete(...)}.
 * - Si en el futuro se requieren búsquedas específicas, pueden añadirse
 *   *query methods* siguiendo la convención de nombres o consultas con {@code @Query}.</p>
 */
@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, String> {
    // JpaRepository ya nos da findById(String numero) y findAll()
}