package Dominio;

import java.util.Date;
import enums.TipoServicio;

public class ServiciosAdicionales {
    private TipoServicio tipoServicio;
    private String descripcionServicio;
    private float valorServicio;
    private Date fechaConsumo;
    //private Estadia estadia; // CUAL DE LOS DOS
    private int idEstadia;

    public ServiciosAdicionales(TipoServicio tipoServicio, String descripcionServicio, float valorServicio, Date fechaConsumo, int idEstadia) {
        this.tipoServicio = tipoServicio;
        this.descripcionServicio = descripcionServicio;
        this.valorServicio = valorServicio;
        this.fechaConsumo = fechaConsumo;
        this.idEstadia = idEstadia;
    }

    //setters y getters
    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }
    public void setTipoServicio(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }
    public String getDescripcionServicio() {
        return descripcionServicio;
    }
    public void setDescripcionServicio(String descripcionServicio) {
        this.descripcionServicio = descripcionServicio;
    }
    public float getValorServicio() {
        return valorServicio;
    }
    public void setValorServicio(float valorServicio) {
        this.valorServicio = valorServicio;
    }
    public Date getFechaConsumo() {
        return fechaConsumo;
    }
    public void setFechaConsumo(Date fechaConsumo) {
        this.fechaConsumo = fechaConsumo;
    }
    /*public Estadia getEstadia() {
        return estadia;
    }
    public void setEstadia(Estadia estadia) {
        this.estadia = estadia;
    }*/
    public int getIdEstadia() {
        return idEstadia;
    }
    public void setIdEstadia(int idEstadia) {
        this.idEstadia = idEstadia;
    }
}
