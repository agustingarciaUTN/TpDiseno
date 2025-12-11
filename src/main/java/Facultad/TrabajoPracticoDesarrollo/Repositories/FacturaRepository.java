package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.ResponsablePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, String> {

    // Método útil: Buscar facturas por el ID de la estadía asociada
    List<Factura> findByEstadia_IdEstadia(Integer idEstadia);

    // Método útil: Buscar facturas pendientes de un responsable específico
    // Asumiendo que el enum se llama PENDIENTE
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Factura f WHERE f.responsablePago.idResponsable = :idResponsable AND f.estadoFactura = Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura.PENDIENTE")
    List<Factura> buscarFacturasPendientesPorResponsable(Integer idResponsable);

    @Modifying
    @Query("UPDATE Factura f SET f.responsablePago = :huespedDestino WHERE f.responsablePago = :huespedOriginal")
    void migrarFacturas(
            @Param("huespedOriginal") ResponsablePago huespedOriginal,
            @Param("huespedDestino") ResponsablePago huespedDestino
    );

    // En FacturaRepository
    boolean existsByResponsablePago(ResponsablePago responsable);

}