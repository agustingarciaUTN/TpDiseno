package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.EstadiaHuesped;
import Facultad.TrabajoPracticoDesarrollo.Dominio.EstadiaHuespedId;
import Facultad.TrabajoPracticoDesarrollo.Dominio.Huesped;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadiaHuespedRepository extends JpaRepository<EstadiaHuesped, EstadiaHuespedId> {


    boolean existsByHuesped(Huesped huesped);


    @Modifying
    @Query(value = "UPDATE estadia_huesped " +
            "SET tipo_documento = :nuevoTipo, nro_documento = :nuevoNro " +
            "WHERE tipo_documento = :viejoTipo AND nro_documento = :viejoNro",
            nativeQuery = true)
    void migrarHistorialEstadias(
            @Param("viejoTipo") String viejoTipo,  // <--- Verificar @Param
            @Param("viejoNro") String viejoNro,
            @Param("nuevoTipo") String nuevoTipo,
            @Param("nuevoNro") String nuevoNro
    );
}