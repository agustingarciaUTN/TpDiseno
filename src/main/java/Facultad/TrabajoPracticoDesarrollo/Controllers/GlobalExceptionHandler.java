package Facultad.TrabajoPracticoDesarrollo.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * Maneja las excepciones de validación lanzadas cuando un DTO anotado con
     * {@code @Valid} no cumple las restricciones. Recorre los errores de binding
     * y construye un mapa con la forma {@code campo -> mensaje} para devolverlo
     * en la respuesta.
     *
     * @param ex excepción que contiene los errores de validación
     * @return ResponseEntity con un {@link Map} donde cada clave es el nombre del campo
     *         y el valor es el mensaje de error; status {@link HttpStatus#BAD_REQUEST} (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();

        // Recorremos todos los campos que fallaron y guardamos: "campo" -> "mensaje de error"
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });

        // Devolvemos un 400 Bad Request con el mapa de errores
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handler genérico para excepciones no controladas por handlers más específicos.
     *
     * <p>Devuelve un mensaje simple con el detalle de la excepción y el código
     * HTTP {@link HttpStatus#INTERNAL_SERVER_ERROR} (500). Puede extenderse para
     * registrar errores o formatear la respuesta en JSON más compleja.</p>
     *
     * @param ex excepción capturada
     * @return ResponseEntity con mensaje de error y status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
        return new ResponseEntity<>("Error del servidor: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}