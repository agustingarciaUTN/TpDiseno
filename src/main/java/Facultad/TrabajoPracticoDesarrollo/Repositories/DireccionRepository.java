package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// DireccionRepository.java
@Repository
public interface DireccionRepository extends JpaRepository<Direccion, Integer> {
    // Metodo vacío, heredamos todo lo necesario
}