package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.Moneda;
import Facultad.TrabajoPracticoDesarrollo.enums.RedDePago;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tarjeta")
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

    // Getters y Setters
    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }
    public RedDePago getRedDePago() { return redDePago; }
    public void setRedDePago(RedDePago redDePago) { this.redDePago = redDePago; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public Integer getCodigoSeguridad() { return codigoSeguridad; }
    public void setCodigoSeguridad(Integer codigoSeguridad) { this.codigoSeguridad = codigoSeguridad; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public Moneda getMoneda() { return moneda; }
    public void setMoneda(Moneda moneda) { this.moneda = moneda; }
    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }
}