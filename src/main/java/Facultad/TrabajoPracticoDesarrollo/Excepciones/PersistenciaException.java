package Facultad.TrabajoPracticoDesarrollo.Excepciones;
/**
 * Excepción que representa un fallo en la capa de persistencia.
 *
 * <p>Se utiliza para encapsular errores provenientes del acceso a datos (por ejemplo,
 * fallos en la base de datos, problemas de IO o errores del repositorio) y propagar
 * información relevante hacia capas superiores sin exponer detalles internos.</p>
 *
 * <p>Contiene un mensaje descriptivo y la causa original (otra excepción) para preservar
 * la traza y facilitar el diagnóstico del problema.</p>
 */
public class PersistenciaException extends Exception {

    /**
     * Construye una nueva {@code PersistenciaException} con un mensaje y la causa original.
     *
     * @param message mensaje descriptivo del error; se recomienda no ser {@code null}
     * @param cause excepción original que provocó este error; puede ser {@code null}
     */
    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
