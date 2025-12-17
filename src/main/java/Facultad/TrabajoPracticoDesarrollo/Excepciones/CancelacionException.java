package Facultad.TrabajoPracticoDesarrollo.Excepciones;

/**
 * Excepción que representa la cancelación de una operación por parte del usuario.
 *
 * <p>Esta excepción se lanza cuando una acción solicitada es interrumpida o cancelada
 * explícitamente por el usuario y no por un fallo del sistema. Permite distinguir
 * una cancelación intencional de otras condiciones de error.</p>
 *
 * <p>Mensaje por defecto: "Operación cancelada por el usuario."</p>
 */
public class CancelacionException extends Exception{
    /**
     * Crea una nueva instancia de {@code CancelacionException} con un mensaje por defecto.
     *
     * <p>Usar esta excepción cuando se quiera propagar la información de que el usuario
     * canceló la operación y no se produjo un error inesperado.</p>
     */
    public CancelacionException(){
        super("Operación cancelada por el usuario.");
    }

}
