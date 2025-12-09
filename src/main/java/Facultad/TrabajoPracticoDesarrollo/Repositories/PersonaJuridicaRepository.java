package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.PersonaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaJuridicaRepository extends JpaRepository<PersonaJuridica, Integer> {
    // Al guardar, JPA maneja 'responsable_pago', 'persona_juridica'
    // Y TAMBIÉN la tabla satélite de teléfonos (@ElementCollection) automáticamente.

    // Podrías agregar búsqueda por CUIT si la necesitas:
    boolean existsByCuit(String cuit);
    PersonaJuridica findByCuit(String cuit);
}