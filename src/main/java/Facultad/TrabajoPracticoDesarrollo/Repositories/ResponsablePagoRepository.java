package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.ResponsablePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsablePagoRepository extends JpaRepository <ResponsablePago, Integer> {
    // Cuando llames a findById(5), JPA hará el JOIN automáticamente
    // y te devolverá una instancia de PersonaFisica o PersonaJuridica según corresponda.
    @Query("SELECT pj.idResponsable FROM PersonaJuridica pj WHERE pj.cuit = :cuit")
    Integer buscarIdPorCuit(@Param("cuit") String cuit);
}
