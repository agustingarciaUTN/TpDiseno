package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.EstadiaHuesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.EstadiaHuespedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadiaHuespedRepository extends JpaRepository<EstadiaHuesped, EstadiaHuespedId> {
}
