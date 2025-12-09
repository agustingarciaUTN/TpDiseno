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

    @Column(name = "monto")
    private Double monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda")
    private Moneda moneda;

    @Column(name = "fecha_pago")
    @Temporal(TemporalType.DATE)
    private Date fechaPago;

    public Tarjeta() {}

}