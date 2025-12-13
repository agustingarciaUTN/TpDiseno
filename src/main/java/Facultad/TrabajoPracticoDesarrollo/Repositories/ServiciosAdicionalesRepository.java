package Facultad.TrabajoPracticoDesarrollo.Repositories;


import Facultad.TrabajoPracticoDesarrollo.Dominio.ServiciosAdicionales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiciosAdicionalesRepository extends JpaRepository<ServiciosAdicionales, Integer> {
    List<ServiciosAdicionales> findByEstadia_IdEstadia(int idEstadia);
}
