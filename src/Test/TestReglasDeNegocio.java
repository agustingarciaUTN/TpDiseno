package Test;

import Habitacion.GestorHabitacion;
import Reserva.DtoReserva;
import Reserva.GestorReserva;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestReglasDeNegocio {

    @Test
    @DisplayName("Debe rechazar rangos de fecha mayores a 60 días")
    public void testRangoFechasExcesivo() {
        // 1. Preparar Datos (Arrange)
        Calendar cal = Calendar.getInstance();
        Date inicio = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 65); // Sumamos 65 días
        Date fin = cal.getTime();

        // 2. Ejecutar (Act)
        boolean resultado = GestorHabitacion.getInstance().validarRangoFechas(inicio, fin);

        // 3. Verificar (Assert)
        // Esperamos que sea FALSE. Si es TRUE, el test falla y te avisa.
        assertFalse(resultado, "El gestor debería haber rechazado un rango de 65 días");
    }

    @Test
    @DisplayName("Debe detectar errores cuando faltan datos obligatorios en la reserva")
    public void testValidacionDatosIncompletos() {
        // 1. Preparar
        DtoReserva reservaSinDatos = new DtoReserva(); // Todo null
        List<DtoReserva> lista = new ArrayList<>();
        lista.add(reservaSinDatos);

        // 2. Ejecutar
        List<String> errores = GestorReserva.getInstance().validarDatosReserva(lista);

        // 3. Verificar
        assertFalse(errores.isEmpty(), "La lista de errores no debería estar vacía");
        assertTrue(errores.contains("El nombre del responsable es obligatorio."), "Debe pedir el nombre");

        // Podemos imprimir para debug, pero el Assert es el que manda
        System.out.println("Errores detectados: " + errores);
    }
}