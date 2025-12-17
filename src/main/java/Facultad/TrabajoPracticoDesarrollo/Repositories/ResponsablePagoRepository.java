package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.ResponsablePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la jerarquía de entidades que extienden {@link ResponsablePago}.
 *
 * <p>Proporciona operaciones CRUD y de paginación heredadas de {@link JpaRepository}.
 * La entidad raíz es {@code ResponsablePago} y la clave primaria es de tipo {@code Integer}.</p>
 *
 * <p>Notas:
 * - Métodos estándar como {@code findById}, {@code findAll}, {@code save}, {@code delete} son
 *   implementados automáticamente por Spring Data JPA.</p>
 *
 * <p>Comportamiento de herencia/polimorfismo:
 * - Si existen subclases de {@code ResponsablePago} (por ejemplo {@code PersonaFisica} y {@code PersonaJuridica}),
 *   al invocar {@code findById(id)} JPA realizará los JOIN necesarios y devolverá la instancia concreta
 *   correspondiente a la fila encontrada (PersonaFisica o PersonaJuridica) según el mapeo de herencia configurado.</p>
 */
@Repository
public interface ResponsablePagoRepository extends JpaRepository <ResponsablePago, Integer> {
    // Cuando llames a findById(5), JPA hará el JOIN automáticamente
    // y te devolverá una instancia de PersonaFisica o PersonaJuridica según corresponda.
    @Query("SELECT pj.idResponsable FROM PersonaJuridica pj WHERE pj.cuit = :cuit")
    Integer buscarIdPorCuit(@Param("cuit") String cuit);

}
