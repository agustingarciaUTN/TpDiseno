package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "tarjeta")
@Getter @Setter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_tarjeta", discriminatorType = DiscriminatorType.STRING, length = 1)
public abstract class Tarjeta {

    @Id
    @Column(name = "numero_tarjeta")
    private String numeroTarjeta;

    @Column(name = "banco")
    private String banco;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"Red_de_pago\"") // Comillas por mayúsculas en BD
    private RedDePago redDePago;

    @Column(name = "fecha_vencimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaVencimiento;

    @Column(name = "codigo_seg")
    private Integer codigoSeguridad;

    public Tarjeta() {}

}