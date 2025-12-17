package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Cheque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link Cheque}.
 *
 * <p>Proporciona operaciones CRUD y de consulta para la entidad {@code Cheque}.
 * Hereda la funcionalidad estándar de {@link JpaRepository} utilizando {@code String}
 * como tipo del identificador de la entidad.</p>
 *
 * <p>Comportamiento heredado relevante:
 * - {@code findById(String)} para buscar por id.
 * - {@code findAll()} para listar todos los cheques.
 * - {@code save(...)} para insertar o actualizar entidades.
 * - {@code delete(...)} para eliminar entidades.</p>
 *
 * <p>Notas:
 * - No se definen métodos personalizados en este repositorio por ahora; pueden añadirse
 *   query methods o consultas con {@code @Query} si se requieren búsquedas específicas.
 * - Las operaciones de escritura deben ejecutarse en el contexto transaccional habitual de Spring
 *   (por ejemplo desde servicios anotados con {@code @Transactional}).</p>
 */
@Repository
public interface ChequeRepository extends JpaRepository<Cheque, String> {
}