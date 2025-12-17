package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Pago}.
 *
 * <p>Proporciona operaciones CRUD y de paginación heredadas de {@link JpaRepository}
 * para la entidad {@code Pago} cuyo identificador es de tipo {@code Integer}.</p>
 *
 * <p>Se marca con {@code @Repository} para su detección por Spring y para la traducción
 * de excepciones de persistencia cuando corresponda.</p>
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    // Método extra útil: Buscar todos los pagos de una factura específica
    /**
     * Obtiene todos los pagos asociados a una factura identificada por su número.
     *
     * <p>Este es un query method derivado que Spring Data JPA resuelve automáticamente
     * a partir del nombre del método: {@code findByFactura_NumeroFactura}.</p>
     *
     * @param numeroFactura número de la factura por la que se filtran los pagos
     * @return lista de {@link Pago} asociados a la factura; devuelve lista vacía si no hay coincidencias
     */
    List<Pago> findByFactura_NumeroFactura(String numeroFactura);
}