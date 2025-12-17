package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.ResponsablePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Factura}.
 *
 * <p>Proporciona operaciones CRUD y consultas específicas para la gestión de facturas.
 * La clave primaria de {@code Factura} es de tipo {@code String} (por ejemplo: número de factura).</p>
 *
 * <p>Notas:
 * - Métodos de consulta simples se derivan por convención de nombre (query methods).
 * - Las consultas anotadas con {@code @Modifying} realizan operaciones de escritura
 *   y deben ejecutarse dentro de una transacción (p. ej. desde un servicio anotado
 *   con {@code @Transactional}).</p>
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, String> {

    // Método útil: Buscar facturas por el ID de la estadía asociada
    /**
     * Busca todas las facturas asociadas a una estadía identificada por su id.
     *
     * @param idEstadia identificador de la estadía
     * @return lista de {@link Factura} relacionadas con la estadía; lista vacía si no hay resultados
     */
    List<Factura> findByEstadia_IdEstadia(Integer idEstadia);

    /**
     * Busca facturas en estado {@code PENDIENTE} cuyo responsable tiene el id indicado.
     *
     * <p>La consulta utiliza la enumeración {@code Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura}
     * y filtra por el estado {@code PENDIENTE} de forma explícita.</p>
     *
     * @param idResponsable identificador del responsable de pago
     * @return lista de facturas pendientes para el responsable indicado
     */
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Factura f WHERE f.responsablePago.idResponsable = :idResponsable AND f.estadoFactura = Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura.PENDIENTE")
    List<Factura> buscarFacturasPendientesPorResponsable(Integer idResponsable);


    /**
     * Migra la asociación de facturas desde un responsable de pago original hacia un responsable destino.
     *
     * <p>Operación de actualización en masa; la anotación {@code @Modifying} indica que la consulta
     * modifica datos. Debe ejecutarse dentro de una transacción para garantizar la atomicidad.</p>
     *
     * @param huespedOriginal responsable de pago actual a reemplazar
     * @param huespedDestino  responsable de pago que recibirá las facturas
     */
    @Modifying
    @Query("UPDATE Factura f SET f.responsablePago = :huespedDestino WHERE f.responsablePago = :huespedOriginal")
    void migrarFacturas(
            @Param("huespedOriginal") ResponsablePago huespedOriginal,
            @Param("huespedDestino") ResponsablePago huespedDestino
    );

    // En FacturaRepository
    /**
     * Comprueba si existen facturas asociadas a un {@link ResponsablePago} dado.
     *
     * @param responsable responsable de pago a verificar
     * @return {@code true} si existe al menos una factura con ese responsable; {@code false} en caso contrario
     */

    boolean existsByResponsablePago(ResponsablePago responsable);

    /**
     * Comprueba si existe una factura con el número de factura indicado.
     *
     * @param numeroFactura número de factura a verificar
     * @return {@code true} si ya existe una factura con ese número; {@code false} en caso contrario
     */
    boolean existsByNumeroFactura(String numeroFactura);

    /**
     * Obtiene la factura con el mayor valor en {@code numeroFactura}.
     *
     * <p>Útil para determinar el último número de factura generado (p. ej. para secuenciar números).</p>
     *
     * @return la {@link Factura} con el número de factura más alto, o {@code null} si no hay facturas
     */
    Factura findTopByOrderByNumeroFacturaDesc();

}