package Dominio;

import enums.TipoCama;

public class Cama {

private int idCama;
private TipoCama tipoCama;
private String idHabitacion; // CUAL DE LOS DOS
private Habitacion habitacion; // CUAL DE LOS DOS

public Cama(int idCama, String idHabitacion) {
    this.idCama = idCama;
    this.idHabitacion = idHabitacion;
}

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
public String getIdHabitacion() {
    return idHabitacion;
}
public void setIdHabitacion(String idHabitacion) {
    this.idHabitacion = idHabitacion;
}
public Habitacion getHabitacion() {
    return habitacion;
}
public void setHabitacion(Habitacion habitacion) {
    this.habitacion = habitacion;
}
}