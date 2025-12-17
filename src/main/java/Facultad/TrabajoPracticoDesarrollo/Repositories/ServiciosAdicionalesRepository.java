package Facultad.TrabajoPracticoDesarrollo.Repositories;


import Facultad.TrabajoPracticoDesarrollo.Dominio.ServiciosAdicionales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link ServiciosAdicionales}.
 *
 * <p>Proporciona operaciones CRUD y de paginación heredadas de {@link JpaRepository}
 * para la entidad {@code ServiciosAdicionales} cuyo identificador es de tipo {@code Integer}.</p>
 *
 * <p>Incluye consultas derivadas (query methods) que Spring Data resuelve automáticamente
 * a partir del nombre del método.</p>
 */
@Repository
public interface ServiciosAdicionalesRepository extends JpaRepository<ServiciosAdicionales, Integer> {
    /**
     * Obtiene la lista de servicios adicionales asociados a una estadía determinada.
     *
     * <p>La consulta se deriva del nombre del método: {@code findByEstadia_IdEstadia} busca
     * por la propiedad `estadia.idEstadia` de la entidad {@code ServiciosAdicionales}.</p>
     *
     * @param idEstadia identificador de la estadía por la que se filtran los servicios adicionales
     * @return lista de {@link ServiciosAdicionales} asociados a la estadía. Si no existen coincidencias,
     *         se devuelve una lista vacía (no {@code null}).
     */
    List<ServiciosAdicionales> findByEstadia_IdEstadia(int idEstadia);
}