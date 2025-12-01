package Dominio;

import enums.TipoCama;

public class Cama {

private int idCama;
private TipoCama tipoCama;
private Habitacion habitacion;

    // --- CONSTRUCTOR PRIVADO (Builder) ---
    private Cama(Builder builder) {
    this.idCama = builder.idCama;
    this.tipoCama = builder.tipoCama;
    this.habitacion = builder.habitacion;
}

    // Constructor por defecto
    public Cama() {}

//setters y getters
public int getIdCama() {
    return idCama;
}
public void setIdCama(int idCama) {
    this.idCama = idCama;
}
public TipoCama getTipoCama() {
    return tipoCama;
}
public void setTipoCama(TipoCama tipoCama) {
    this.tipoCama = tipoCama;
}
public Habitacion getHabitacion() {
    return habitacion;
}
public void setHabitacion(Habitacion habitacion) {
    this.habitacion = habitacion;
}

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private int idCama;
        private TipoCama tipoCama;
        private Habitacion habitacion;

        public Builder() {}

        public Builder idCama(int val) { idCama = val; return this; }
        public Builder tipoCama(TipoCama val) { tipoCama = val; return this; }
        public Builder habitacion(Habitacion val) { habitacion = val; return this; }

        public Cama build() {
            return new Cama(this);
        }
    }

}