package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la entidad {@link PersonaJuridica}.
 *
 * <p>Provee operaciones CRUD y de paginación heredadas de {@link JpaRepository}
 * para la entidad {@code PersonaJuridica} cuyo identificador es de tipo {@code Integer}.</p>
 *
 * <p>Comportamiento y notas de persistencia:
 * - Al guardar una instancia de {@code PersonaJuridica}, JPA gestiona automáticamente
 *   los inserts/updates en las tablas asociadas: la tabla base de {@code responsable_pago},
 *   la tabla específica de {@code persona_juridica} y cualquier tabla satélite (por ejemplo
 *   la colección de teléfonos mapeada con {@code @ElementCollection}).</p>
 *
 * <p>Se marca con {@code @Repository} para que Spring la detecte como componente de persistencia
 * y aplique la traducción de excepciones de persistencia cuando corresponda.</p>
 */
@Repository
public interface PersonaJuridicaRepository extends JpaRepository<PersonaJuridica, Integer> {
    // Al guardar, JPA maneja 'responsable_pago', 'persona_juridica'
    // Y TAMBIÉN la tabla satélite de teléfonos (@ElementCollection) automáticamente.

    // Podrías agregar búsqueda por CUIT si la necesitas:
    /**
     * Comprueba si existe una {@link PersonaJuridica} con el CUIT provisto.
     *
     * @param cuit CUIT a consultar (cadena con formato esperado por la aplicación)
     * @return {@code true} si existe al menos una entidad con ese CUIT, {@code false} en caso contrario
     */
    boolean existsByCuit(String cuit);
    /**
     * Busca una {@link PersonaJuridica} por su CUIT.
     *
     * <p>Este método es una query derivada cuyo comportamiento lo genera Spring Data JPA
     * en tiempo de ejecución a partir del nombre del método.</p>
     *
     * @param cuit CUIT de la persona jurídica a buscar
     * @return la instancia {@link PersonaJuridica} encontrada, o {@code null} si no existe ninguna coincidencia
     */
    PersonaJuridica findByCuit(String cuit);
}