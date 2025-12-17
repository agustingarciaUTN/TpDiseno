package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link Direccion}.
 *
 * <p>Proporciona operaciones CRUD y de consulta para la entidad {@code Direccion}.
 * Hereda la funcionalidad estándar de {@link JpaRepository} usando {@code Integer}
 * como tipo de identificador.</p>
 *
 * <p>Comportamiento heredado relevante:
 * - {@code findById(Integer)} para buscar por id.
 * - {@code findAll()} para listar todas las direcciones.
 * - {@code save(...)} para insertar o actualizar entidades.
 * - {@code delete(...)} para eliminar entidades.</p>
 *
 * <p>Notas:
 * - No se definen métodos personalizados por ahora; añadir query methods o
 *   consultas con {@code @Query} si se requieren búsquedas específicas en el futuro.
 * - Las operaciones de escritura deben ejecutarse en el contexto transaccional
 *   habitual de Spring (p. ej. desde un servicio anotado con {@code @Transactional}).</p>
 */
@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {
    // Metodo vacío, heredamos todo lo necesario de Jpa
}