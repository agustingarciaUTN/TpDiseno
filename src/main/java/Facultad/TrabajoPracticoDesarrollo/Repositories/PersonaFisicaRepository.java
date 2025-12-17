package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaFisicaRepository extends JpaRepository<PersonaFisica, Integer> {
    // Hereda save(), findById(), etc.
    // Al guardar, JPA inserta primero en 'responsable_pago' y luego en 'persona_fisica' automáticamente.


    //Metodo para el CU10, tenemos que obtener la persona fisica equivalente al Huesped
    /**
     * Busca la {@link PersonaFisica} asociada al {@link Huesped} proporcionado.
     *
     * <p>Se utiliza para obtener el rol de persona física equivalente a un huésped
     * (por ejemplo en casos de CU10 donde se requiere mapear entidades relacionadas).</p>
     *
     * @param huesped entidad {@link Huesped} usada como criterio de búsqueda
     * @return {@link Optional} que contiene la {@link PersonaFisica} si existe, o {@code Optional.empty()} si no
     */
    Optional<PersonaFisica> findByHuesped(Huesped huesped);

    /**
     * Busca la {@link PersonaFisica} por el tipo y número de documento del {@link Huesped}.
     *
     * <p>Método derivado que resuelve la ruta de propiedades {@code huesped.tipoDocumento}
     * y {@code huesped.nroDocumento} para efectuar la búsqueda.</p>
     *
     * @param tipo tipo de documento (enum {@link TipoDocumento})
     * @param nro  número de documento como {@link String}
     * @return {@link Optional} con la {@link PersonaFisica} encontrada, o {@code Optional.empty()} si no existe
     */
    Optional<PersonaFisica> findByHuesped_TipoDocumentoAndHuesped_NroDocumento(
            TipoDocumento tipo,
            String nro
    );
    // Para borrar el rol de pagador asociado (CU11)
    //void deleteByHuesped(Huesped huesped);

    /**
     * Elimina de forma nativa la fila correspondiente en la tabla {@code persona_fisica}
     * dada la clave foránea {@code id_responsable}.
     *
     * <p>Este método ejecuta una consulta nativa {@code DELETE} y por ello debe invocarse
     * dentro de una transacción. Es una operación de modificación, por eso está anotada con {@link Modifying}.</p>
     *
     * @param id identificador {@code id_responsable} cuyo registro de {@code persona_fisica} se desea eliminar
     */
    @Modifying
    @Query(value = "DELETE FROM persona_fisica WHERE id_responsable = :id", nativeQuery = true)
    void borrarNativo(@Param("id") Integer id);
}