package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "nota_de_credito")
@Getter @Setter
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

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private Integer numeroNotaCredito;
        private Double montoDevolucion;

        public Builder() {}

        public Builder (Integer numeroNotaCredito, double montoDevolucion) {}

        // El ID puede ser nulo al crear si es autoincremental
        public Builder numero(Integer val) { numeroNotaCredito = val; return this; }
        public Builder monto(Double val) { montoDevolucion = val; return this; }

        public NotaDeCredito build() {
            return new NotaDeCredito(this);
        }
    }
}