package Habitacion;

import enums.TipoCama;

public class DtoCama {
    private int idCama;
    private TipoCama tipoCama;
    private String idHabitacion;
    private DtoHabitacion habitacion;

    // --- CONSTRUCTOR PRIVADO (Builder) ---
    private DtoCama(Builder builder) {
        this.idCama = builder.idCama;
        this.tipoCama = builder.tipoCama;
        this.idHabitacion = builder.idHabitacion;
    }

    // Constructor por defecto
    public DtoCama() {}

    // --- GETTERS Y SETTERS ---
    public int getIdCama() { return idCama; }
    public void setIdCama(int idCama) { this.idCama = idCama; }

    public TipoCama getTipoCama() { return tipoCama; }
    public void setTipoCama(TipoCama tipoCama) { this.tipoCama = tipoCama; }

    public String getIdHabitacion() { return idHabitacion; }
    public void setIdHabitacion(String idHabitacion) { this.idHabitacion = idHabitacion; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idCama;
        private TipoCama tipoCama;
        private String idHabitacion;

        public Builder() {}

        public Builder idCama(int val) { idCama = val; return this; }
        public Builder tipoCama(TipoCama val) { tipoCama = val; return this; }
        public Builder idHabitacion(String val) { idHabitacion = val; return this; }

        public DtoCama build() {
            return new DtoCama(this);
        }
    }
}
