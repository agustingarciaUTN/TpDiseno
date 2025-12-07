package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "nota_de_credito")
public class NotaDeCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_nota_credito")
    private Integer numeroNotaCredito; // Integer para coincidir con SERIAL de BD

    @Column(name = "monto_devolucion")
    private Double montoDevolucion;

    // --- CONSTRUCTOR PRIVADO (Builder) ---
    private NotaDeCredito(Builder builder) {
        this.numeroNotaCredito = builder.numeroNotaCredito;
        this.montoDevolucion = builder.montoDevolucion;
    }

    // Constructor por defecto (Obligatorio JPA)
    public NotaDeCredito() {}

    // --- GETTERS Y SETTERS ---
    public Integer getNumeroNotaCredito() { return numeroNotaCredito; }
    public void setNumeroNotaCredito(Integer numeroNotaCredito) { this.numeroNotaCredito = numeroNotaCredito; }

    public Double getMontoDevolucion() { return montoDevolucion; }
    public void setMontoDevolucion(Double montoDevolucion) { this.montoDevolucion = montoDevolucion; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private Integer numeroNotaCredito;
        private Double montoDevolucion;

        public Builder() {}

        // El ID puede ser nulo al crear si es autoincremental
        public Builder numero(Integer val) { numeroNotaCredito = val; return this; }
        public Builder monto(Double val) { montoDevolucion = val; return this; }

        public NotaDeCredito build() {
            return new NotaDeCredito(this);
        }
    }
}