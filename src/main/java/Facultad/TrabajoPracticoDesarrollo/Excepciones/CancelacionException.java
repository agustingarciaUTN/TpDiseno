package Facultad.TrabajoPracticoDesarrollo.Excepciones;

public class CancelacionException extends Exception{
    public CancelacionException(){
        super("Operación cancelada por el usuario.");
    }

}
