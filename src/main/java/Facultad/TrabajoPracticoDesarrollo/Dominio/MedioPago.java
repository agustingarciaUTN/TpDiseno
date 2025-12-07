package Facultad.TrabajoPracticoDesarrollo.Dominio;

import jakarta.persistence.*;

@Entity
@Table(name = "medio_pago")
public class MedioPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medio_pago")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pago")
    private Pago pago;

    // --- RELACIONES EXCLUSIVAS (Uno de estos 3 estará lleno) ---

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_efectivo")
    private Efectivo efectivo;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "numero_cheque")
    private Cheque cheque;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "numero_tarjeta")
    private Tarjeta tarjeta;

    public MedioPago() {}

    // Builder
    public static class Builder {
        private Pago pago;
        private Efectivo efectivo;
        private Cheque cheque;
        private Tarjeta tarjeta;

        public Builder() {}
        public Builder pago(Pago val) { pago = val; return this; }
        public Builder efectivo(Efectivo val) { efectivo = val; return this; }
        public Builder cheque(Cheque val) { cheque = val; return this; }
        public Builder tarjeta(Tarjeta val) { tarjeta = val; return this; }

        public MedioPago build() {
            MedioPago mp = new MedioPago();
            mp.setPago(pago);
            mp.setEfectivo(efectivo);
            mp.setCheque(cheque);
            mp.setTarjeta(tarjeta);
            return mp;
        }
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }
    public Efectivo getEfectivo() { return efectivo; }
    public void setEfectivo(Efectivo efectivo) { this.efectivo = efectivo; }
    public Cheque getCheque() { return cheque; }
    public void setCheque(Cheque cheque) { this.cheque = cheque; }
    public Tarjeta getTarjeta() { return tarjeta; }
    public void setTarjeta(Tarjeta tarjeta) { this.tarjeta = tarjeta; }
}