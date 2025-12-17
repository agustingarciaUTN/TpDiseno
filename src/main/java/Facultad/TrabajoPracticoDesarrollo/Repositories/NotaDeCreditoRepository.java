package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.NotaDeCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repositorio Spring Data JPA para la entidad {@link NotaDeCredito}.
 *
 * <p>Provee operaciones CRUD y de paginación heredadas de {@link JpaRepository}
 * para la entidad {@code NotaDeCredito} cuyo identificador es de tipo {@code Integer}.</p>
 *
 * <p>Notas:
 * - Métodos como {@code save}, {@code findById}, {@code findAll}, {@code delete} son provistos
 *   automáticamente por Spring Data JPA y no requieren implementación manual.
 * - Si se necesitan búsquedas o actualizaciones específicas, pueden añadirse *query methods*
 *   (por convención de nombres) o consultas annotadas con {@code @Query}.</p>
 *
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface NotaDeCreditoRepository extends JpaRepository<NotaDeCredito, Integer> {
    // JpaRepository ya provee save(), findById(), findAll(), etc.
}