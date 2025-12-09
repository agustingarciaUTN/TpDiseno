package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// La clave primaria de Tarjeta es String (numeroTarjeta)
@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, String> {
    // Al guardar una entidad "TarjetaCredito", JPA inserta en 'tarjeta' y 'tarjeta_credito'.
    // Al guardar una "TarjetaDebito", inserta en 'tarjeta' y 'tarjeta_debito'.
}