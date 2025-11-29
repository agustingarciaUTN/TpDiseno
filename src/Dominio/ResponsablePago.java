package Dominio;

public abstract class ResponsablePago {
    
    private int idResponsablePago;

    public ResponsablePago(int idResponsablePago) {
        if(idResponsablePago <= 0) {
            throw new IllegalArgumentException("El ID del responsable de pago debe ser un número positivo.");
        }
        this.idResponsablePago = idResponsablePago;
        
    }

    // Getters y Setters
    public int getIdResponsablePago() {
        return idResponsablePago;
    }
    public void setIdResponsablePago(int idResponsablePago) {
        this.idResponsablePago = idResponsablePago;
    }

}
