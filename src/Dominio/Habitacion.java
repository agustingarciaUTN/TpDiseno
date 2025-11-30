package Dominio;

import enums.TipoHabitacion;
import enums.EstadoHabitacion;
import java.util.ArrayList;

public class Habitacion {

    private String numero;
    private TipoHabitacion tipoHabitacion;
    private int capacidad;
    private EstadoHabitacion estadoHabitacion;
    private float costoPorNoche;
    private ArrayList<Reserva> reservas;

    // --- CONSTRUCTOR PRIVADO ---
    private Habitacion(Builder builder) {
        this.numero = builder.numero;
        this.tipoHabitacion = builder.tipoHabitacion;
        this.capacidad = builder.capacidad;
        this.estadoHabitacion = builder.estadoHabitacion;
        this.costoPorNoche = builder.costoPorNoche;
        this.reservas = builder.reservas;
    }

    // Constructor por defecto (opcional, por compatibilidad)
    public Habitacion() {}

    // --- GETTERS Y SETTERS ---
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public TipoHabitacion getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public EstadoHabitacion getEstadoHabitacion() { return estadoHabitacion; }
    public void setEstadoHabitacion(EstadoHabitacion estadoHabitacion) { this.estadoHabitacion = estadoHabitacion; }

    public float getCostoPorNoche() { return costoPorNoche; }
    public void setCostoPorNoche(float costoPorNoche) { this.costoPorNoche = costoPorNoche; }

    public ArrayList<Reserva> getReservas() { return reservas; }
    public void setReservas(ArrayList<Reserva> reservas) { this.reservas = reservas; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private String numero;
        private TipoHabitacion tipoHabitacion;
        private int capacidad;

        // Opcionales
        private EstadoHabitacion estadoHabitacion;
        private float costoPorNoche;
        private ArrayList<Reserva> reservas = new ArrayList<>();

        // Constructor con los datos OBLIGATORIOS
        public Builder(String numero, TipoHabitacion tipo, int capacidad) {
            this.numero = numero;
            this.tipoHabitacion = tipo;
            this.capacidad = capacidad;
        }

        public Builder estado(EstadoHabitacion val) { estadoHabitacion = val; return this; }
        public Builder costo(float val) { costoPorNoche = val; return this; }

        public Builder reservas(ArrayList<Reserva> val) { reservas = val; return this; }
        public Builder agregarReserva(Reserva val) {
            if (this.reservas == null) this.reservas = new ArrayList<>();
            this.reservas.add(val);
            return this;
        }

        public Habitacion build() {
            // Validaciones
            if (numero == null || numero.isEmpty()) {
                throw new IllegalArgumentException("El número de habitación es obligatorio.");
            }
            if (capacidad <= 0) {
                throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
            }
            return new Habitacion(this);
        }
    }
}