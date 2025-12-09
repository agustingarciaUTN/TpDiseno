package Facultad.TrabajoPracticoDesarrollo.Repositories;

import Facultad.TrabajoPracticoDesarrollo.Dominio.MedioPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedioDePagoRepository extends JpaRepository<MedioPago, Integer> {
    // Gracias al mapeo JPA en la entidad MedioPago:
    // @OneToOne(cascade = CascadeType.ALL) private Tarjeta tarjeta;
    // @OneToOne(cascade = CascadeType.ALL) private Cheque cheque;
    // @OneToOne(cascade = CascadeType.ALL) private Efectivo efectivo;

    // Al guardar un MedioPago usando este repositorio (o por cascada desde Pago),
    // Hibernate detecta automáticamente cuál de los 3 tipos tiene datos
    // e inserta en la tabla correspondiente (tarjeta, cheque o efectivo).
}