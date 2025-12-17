package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.ResponsablePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsablePagoRepository extends JpaRepository <ResponsablePago, Integer> {

    @Query("SELECT pj.idResponsable FROM PersonaJuridica pj WHERE pj.cuit = :cuit")
    Integer buscarIdPorCuit(@Param("cuit") String cuit);

}
