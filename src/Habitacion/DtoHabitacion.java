package Habitacion;

import Dominio.Reserva;
import enums.EstadoHabitacion;
import enums.TipoHabitacion;
import Reserva.DtoReserva;

import java.util.ArrayList;

public class DtoHabitacion {
    private String numero;
    private TipoHabitacion tipoHabitacion;
    private int capacidad;

    // Opcionales
    private EstadoHabitacion estadoHabitacion;
    private float costoPorNoche;
    private ArrayList<DtoReserva> dtoReservas = new ArrayList<>();

    // --- CONSTRUCTOR PRIVADO ---
    private DtoHabitacion(DtoHabitacion.Builder builder) {
        this.numero = builder.numero;
        this.tipoHabitacion = builder.tipoHabitacion;
        this.capacidad = builder.capacidad;
        this.estadoHabitacion = builder.estadoHabitacion;
        this.costoPorNoche = builder.costoPorNoche;
        this.dtoReservas = builder.dtoReservas;
    }
    // Constructor por defecto (opcional, por compatibilidad)
    public DtoHabitacion() {}

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

    public ArrayList<DtoReserva> getReservas() { return dtoReservas; }
    public void setReservas(ArrayList<DtoReserva> reservas) { this.dtoReservas = reservas; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private String numero;
        private TipoHabitacion tipoHabitacion;
        private int capacidad;

        // Opcionales
        private EstadoHabitacion estadoHabitacion;
        private float costoPorNoche;
        private ArrayList<DtoReserva> dtoReservas = new ArrayList<>();

        // Constructor con los datos OBLIGATORIOS
        public Builder(String numero, TipoHabitacion tipo, int capacidad) {
            this.numero = numero;
            this.tipoHabitacion = tipo;
            this.capacidad = capacidad;
        }

        public DtoHabitacion.Builder estado(EstadoHabitacion val) { estadoHabitacion = val; return this; }
        public DtoHabitacion.Builder costo(float val) { costoPorNoche = val; return this; }

        public DtoHabitacion.Builder reservas(ArrayList<DtoReserva> val) { dtoReservas = val; return this; }
        public DtoHabitacion.Builder agregarReserva(DtoReserva val) {
            if (this.dtoReservas == null) this.dtoReservas = new ArrayList<>();
            this.dtoReservas.add(val);
            return this;
        }

        public DtoHabitacion build() {
            // Validaciones
            if (numero == null || numero.isEmpty()) {
                throw new IllegalArgumentException("El número de habitación es obligatorio.");
            }
            if (capacidad <= 0) {
                throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
            }
            return new DtoHabitacion(this);
        }
    }
}


