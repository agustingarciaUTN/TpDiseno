package Dominio;

import java.util.ArrayList;

public abstract class MedioPago {

    private int idPago;
    private ArrayList<Pago> pagos;

    public MedioPago(int idPago, ArrayList<Pago> pagos) {
        this.idPago = idPago;
        this.pagos = pagos;
    }

    // Getters y Setters
    public int getIdPago() {
        return idPago;
    }
    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }
    public ArrayList<Pago> getPagos() {
        return pagos;
    }
    public void setPagos(ArrayList<Pago> pagos) {
        this.pagos = pagos;
    }
}