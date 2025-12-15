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
    @Query(value = "SELECT DISTINCT h.* FROM huesped h " +
            "LEFT JOIN direccion d ON d.id_direccion = h.id_direccion " +
            "WHERE " +
            "(CAST(:apellido AS TEXT) IS NULL OR :apellido = '' OR LOWER(h.apellido) LIKE LOWER(CONCAT(CAST(:apellido AS TEXT), '%'))) AND " +
            "(CAST(:nombres AS TEXT) IS NULL OR :nombres = '' OR LOWER(h.nombres) LIKE LOWER(CONCAT(CAST(:nombres AS TEXT), '%'))) AND " +
            "(CAST(:tipo AS TEXT) IS NULL OR CAST(h.tipo_documento AS TEXT) = CAST(:tipo AS TEXT)) AND " +
            "(CAST(:nroDoc AS TEXT) IS NULL OR :nroDoc = '' OR h.numero_documento LIKE CONCAT(CAST(:nroDoc AS TEXT), '%'))",
            nativeQuery = true)
    List<Huesped> buscarPorCriterios(
            @Param("apellido") String apellido,
            @Param("nombres") String nombres,
            @Param("tipo") String tipo,
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