package Excepciones;

public class PersistenciaException extends Exception {
    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
