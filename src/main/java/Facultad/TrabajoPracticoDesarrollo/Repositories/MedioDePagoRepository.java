package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.MedioPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link MedioPago}.
 *
 * <p>Provee operaciones CRUD y de paginación heredadas de {@link JpaRepository}
 * para la entidad {@code MedioPago} cuyo identificador es de tipo {@code Integer}.</p>
 *
 * <p>Notas de persistencia y mapeo:
 * - La entidad {@code MedioPago} modela una relación uno-a-uno con los distintos
 *   tipos concretos de pago (por ejemplo: {@code Tarjeta}, {@code Cheque}, {@code Efectivo}).
 * - Al usar {@code CascadeType.ALL} en esos mapeos, al persistir un {@code MedioPago}
 *   Hibernate/JPA propagará la operación a la entidad asociada correspondiente.</p>
 *
 * <p>Comportamiento práctico:
 * - Al guardar una instancia de {@code MedioPago} (directamente con este repositorio
 *   o por cascada desde {@code Pago}), JPA/Hibernate detecta cuál de las relaciones
 *   uno-a-uno contiene datos (por ejemplo, {@code tarjeta != null}) y realiza la
 *   inserción/actualización en la tabla correspondiente asociada a ese tipo concreto.</p>
 *
 * <p>Se marca con {@code @Repository} para que Spring la detecte como componente de
 * persistencia y aplique la traducción de excepciones de persistencia cuando corresponda.</p>
 */
@Repository
public interface MedioDePagoRepository extends JpaRepository<MedioPago, Integer> {
    // Gracias al mapeo JPA en la entidad MedioPago:
    // @OneToOne(cascade = CascadeType.ALL) private Tarjeta tarjeta;
    // @OneToOne(cascade = CascadeType.ALL) private Cheque cheque;
    // @OneToOne(cascade = CascadeType.ALL) private Efectivo efectivo;

    // Al guardar un MedioPago usando este repositorio (o por cascada desde Pago),
    // Hibernate detecta automáticamente cuál de los 3 tipos tiene datos
    // e inserta en la tabla correspondiente (tarjeta, cheque o efectivo).
}