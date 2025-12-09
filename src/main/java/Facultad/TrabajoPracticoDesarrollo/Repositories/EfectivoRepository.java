package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Efectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EfectivoRepository extends JpaRepository<Efectivo, Integer> {
}