package Test;

import Reserva.DaoReserva;
import Reserva.DtoReserva;
import Dominio.Reserva;
import enums.EstadoReserva;
import org.junit.jupiter.api.*;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

// Usamos @TestMethodOrder para ejecutar en orden si queremos (opcional)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestIntegracionReserva {

    private static DaoReserva dao;
    private static String HABITACION_TEST = "101"; // Asegúrate que esta exista en tu BD
    private static Date fechaInicioBase;
    private static Date fechaFinBase;

    @BeforeAll
    public static void setup() {
        dao = DaoReserva.getInstance();

        // Definimos fechas fijas para el test: del 10 al 20 del mes próximo
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 10);
        fechaInicioBase = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 20);
        fechaFinBase = cal.getTime();

        System.out.println("--- Preparando entorno de pruebas de BD ---");
    }

    @Test
    @Order(1)
    @DisplayName("Insertar una reserva base para probar solapamientos")
    public void testInsertarReservaBase() {
        // Creamos una reserva "semilla" para molestar a las siguientes
        // OJO: Usamos tus entidades y DAOs reales
        // Nota: Aquí tendrías que usar tu lógica completa de creación o insertar manual
        // Para este ejemplo, asumimos que la reserva YA EXISTE o la creamos aquí.

        // Verificamos primero que esté libre para que el test sea determinista
        boolean ocupada = dao.hayReservaEnFecha(HABITACION_TEST, fechaInicioBase, fechaFinBase);

        // Este assert es 'informativo', depende del estado de tu base
        System.out.println("Estado inicial habitación " + HABITACION_TEST + ": " + (ocupada ? "OCUPADA" : "LIBRE"));
    }

    @Test
    @Order(2)
    @DisplayName("Debe detectar Solapamiento Total (Intento reservar fechas que envuelven a la existente)")
    public void testSolapamientoTotal() {
        // Escenario: La reserva existe del 10 al 20.
        // Intento reservar del 05 al 25.

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicioBase);
        cal.add(Calendar.DAY_OF_MONTH, -5); // Día 5
        Date inicioIntento = cal.getTime();

        cal.setTime(fechaFinBase);
        cal.add(Calendar.DAY_OF_MONTH, 5); // Día 25
        Date finIntento = cal.getTime();

        // Ejecutar DAO
        boolean resultado = dao.hayReservaEnFecha(HABITACION_TEST, inicioIntento, finIntento);

        // Assert: DEBE dar TRUE (Ocupada)
        assertTrue(resultado, "Falló: El sistema permitió reservar encima de otra reserva (Solapamiento Total)");
    }

    @Test
    @Order(3)
    @DisplayName("Debe detectar Solapamiento Parcial (Intento reservar fechas que se cruzan al final)")
    public void testSolapamientoParcial() {
        // Escenario: Reserva 10-20.
        // Intento: 15-25.

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaInicioBase);
        cal.add(Calendar.DAY_OF_MONTH, 5); // Día 15 (Dentro de la reserva existente)
        Date inicioIntento = cal.getTime();

        cal.setTime(fechaFinBase);
        cal.add(Calendar.DAY_OF_MONTH, 5); // Día 25 (Fuera)
        Date finIntento = cal.getTime();

        boolean resultado = dao.hayReservaEnFecha(HABITACION_TEST, inicioIntento, finIntento);

        assertTrue(resultado, "Falló: El sistema no detectó el cruce de fechas parcial");
    }

    @Test
    @Order(4)
    @DisplayName("Debe permitir reserva en fechas completamente libres")
    public void testFechasLibres() {
        // Escenario: Reserva 10-20.
        // Intento: 25-30.

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaFinBase);
        cal.add(Calendar.DAY_OF_MONTH, 5); // Día 25
        Date inicioIntento = cal.getTime();

        cal.add(Calendar.DAY_OF_MONTH, 5); // Día 30
        Date finIntento = cal.getTime();

        boolean resultado = dao.hayReservaEnFecha(HABITACION_TEST, inicioIntento, finIntento);

        // Aquí esperamos FALSE (No hay reserva, está libre)
        assertFalse(resultado, "Falló: El sistema marcó como ocupada una fecha que debería estar libre");
    }
}