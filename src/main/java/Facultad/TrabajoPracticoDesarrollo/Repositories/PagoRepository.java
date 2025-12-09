package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {

    // Método extra útil: Buscar todos los pagos de una factura específica
    List<Pago> findByFactura_NumeroFactura(String numeroFactura);
}