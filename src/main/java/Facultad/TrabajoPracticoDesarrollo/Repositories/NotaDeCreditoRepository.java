package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.NotaDeCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaDeCreditoRepository extends JpaRepository<NotaDeCredito, Integer> {
    // JpaRepository ya provee save(), findById(), findAll(), etc.
}