package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Efectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link Efectivo}.
 *
 * <p>Proporciona operaciones CRUD y de paginación heredadas de {@link JpaRepository}
 * para la entidad {@code Efectivo} cuyo identificador es de tipo {@code Integer}.</p>
 *
 * <p>Notas:
 * - No se requieren métodos adicionales por ahora; {@link JpaRepository} expone métodos comunes
 *   como {@code findById(Integer)}, {@code findAll()}, {@code save(...)} y {@code delete(...)}.
 * - Si en el futuro se necesitan búsquedas o actualizaciones específicas, pueden añadirse
 *   métodos derivados por convención de nombres o consultas con {@code @Query}.
 * - Este repositorio no define operaciones modificadoras explícitas, por lo que no hay
 *   consideraciones especiales de transacción en su interfaz; las operaciones de escritura
 *   se gestionan por el propio Spring Data/JPA o desde servicios con {@code @Transactional}.</p>
 */
@Repository
public interface EfectivoRepository extends JpaRepository<Efectivo, Integer> {
}