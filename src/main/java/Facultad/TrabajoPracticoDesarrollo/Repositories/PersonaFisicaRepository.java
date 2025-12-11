package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaFisicaRepository extends JpaRepository<PersonaFisica, Integer> {
    // Hereda save(), findById(), etc.
    // Al guardar, JPA inserta primero en 'responsable_pago' y luego en 'persona_fisica' automáticamente.


    //Metodo para el CU10, tenemos que obtener la persona fisica equivalente al Huesped
    Optional<PersonaFisica> findByHuesped(Huesped huesped);
}