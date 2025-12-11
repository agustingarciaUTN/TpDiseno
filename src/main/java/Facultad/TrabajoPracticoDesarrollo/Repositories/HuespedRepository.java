package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.HuespedId;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HuespedRepository extends JpaRepository<Huesped, HuespedId> {

    /**
     * Reemplaza a 'obtenerHuespedesPorCriterio'.
     * Busca por coincidencia parcial en nombre/apellido y exacta/parcial en documento.
     * Si un parámetro es NULL, se ignora ese filtro (funciona como tu lógica original).
     */
    @Query("SELECT DISTINCT h FROM Huesped h " +
            "LEFT JOIN FETCH h.direccion " +
            "WHERE " +
            "(:apellido IS NULL OR LOWER(h.apellido) LIKE LOWER(CONCAT(:apellido, '%'))) AND " +
            "(:nombres IS NULL OR LOWER(h.nombres) LIKE LOWER(CONCAT(:nombres, '%'))) AND " +
            "(:tipo IS NULL OR h.tipoDocumento = :tipo) AND " +
            "(:nroDoc IS NULL OR h.nroDocumento LIKE CONCAT(:nroDoc, '%'))")
    List<Huesped> buscarPorCriterios(
            @Param("apellido") String apellido,
            @Param("nombres") String nombres,
            @Param("tipo") TipoDocumento tipo,
            @Param("nroDoc") String nroDoc
    );

    // Reemplaza a 'existeHuesped'
    boolean existsByTipoDocumentoAndNroDocumento(TipoDocumento tipo, String nro);

    // Reemplaza a 'obtenerTodosLosHuespedes': ¡Ya existe! Se llama findAll()

    // Reemplaza a 'obtenerHuesped': ¡Ya existe! Se llama findById(new HuespedId(tipo, nro))

    // Reemplaza a 'eliminarHuesped': ¡Ya existe! Se llama deleteById(...)

    // Reemplaza a 'persistir' y 'modificar': ¡Ya existe! Se llama save(...)

    // Para el CU10: Permitir cambio de DNI manteniendo historial
    @Modifying
    @Query("UPDATE Huesped h SET h.tipoDocumento = :nuevoTipo, h.nroDocumento = :nuevoNro " +
            "WHERE h.tipoDocumento = :viejoTipo AND h.nroDocumento = :viejoNro")
    void actualizarIdentidad(
            @Param("viejoTipo") TipoDocumento viejoTipo,
            @Param("viejoNro") String viejoNro,
            @Param("nuevoTipo") TipoDocumento nuevoTipo,
            @Param("nuevoNro") String nuevoNro
    );

    // Cuenta cuántos huéspedes usan esa dirección, para evitar dejar direcciones zombies
    long countByDireccion(Direccion direccion);

}