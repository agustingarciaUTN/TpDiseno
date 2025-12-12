package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.ResponsablePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsablePagoRepository extends JpaRepository <ResponsablePago, Integer> {
    // Cuando llames a findById(5), JPA hará el JOIN automáticamente
    // y te devolverá una instancia de PersonaFisica o PersonaJuridica según corresponda.
}
