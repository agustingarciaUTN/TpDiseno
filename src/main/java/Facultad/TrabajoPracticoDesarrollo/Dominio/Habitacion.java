package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoHabitacion;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoHabitacion;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habitacion")
public class Habitacion {

    @Id
    @Column(name = "numero") // Clave primaria String
    private String numero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_habitacion")
    private TipoHabitacion tipoHabitacion;

    @Column(name = "capacidad")
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_habitacion")
    private EstadoHabitacion estadoHabitacion;

    @Column(name = "\"Costo_por_noche\"") // Ojo con las mayúsculas en tu BD
    private Double costoPorNoche;

    // --- RELACIÓN CON CAMAS (1 a N) ---
    // mappedBy = "habitacion" significa que la clase Cama es la dueña de la relación (tiene la FK)
    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cama> camas = new ArrayList<>();

    // --- RELACIÓN CON RESERVAS (1 a N) ---
    // Para ver todas las reservas de esta habitación.
    // Usamos JsonIgnore o similar en el Controller después para no hacer bucle infinito
    @OneToMany(mappedBy = "habitacion")
    private List<Reserva> reservas = new ArrayList<>();

    public Habitacion() {}

    private Habitacion(Builder builder) {
        this.numero = builder.numero;
        this.tipoHabitacion = builder.tipoHabitacion;
        this.capacidad = builder.capacidad;
        this.estadoHabitacion = builder.estadoHabitacion;
        this.costoPorNoche = (double) builder.costoPorNoche;
        this.reservas = builder.reservas;
    }

    // --- Getters y Setters ---
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public TipoHabitacion getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }

    public Integer getCapacidad() { return capacidad; }
    public void setCapacidad(Integer capacidad) { this.capacidad = capacidad; }

    public EstadoHabitacion getEstadoHabitacion() { return estadoHabitacion; }
    public void setEstadoHabitacion(EstadoHabitacion estadoHabitacion) { this.estadoHabitacion = estadoHabitacion; }

    public Double getCostoPorNoche() { return costoPorNoche; }
    public void setCostoPorNoche(Double costoPorNoche) { this.costoPorNoche = costoPorNoche; }

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }

    public List<Cama> getCamas() { return camas; }
    public void setCamas(List<Cama> camas) { this.camas = camas; }

    // --- BUILDER (Intacto) ---
    public static class Builder {
        private String numero;
        private TipoHabitacion tipoHabitacion;
        private Integer capacidad;
        private EstadoHabitacion estadoHabitacion;
        private float costoPorNoche;
        private List<Reserva> reservas = new ArrayList<>();

        public Builder(String numero, TipoHabitacion tipo, Integer capacidad) {
            this.numero = numero;
            this.tipoHabitacion = tipo;
            this.capacidad = capacidad;
        }

        public Builder estado(EstadoHabitacion val) { estadoHabitacion = val; return this; }
        public Builder costo(float val) { costoPorNoche = val; return this; }

        public Habitacion build() { return new Habitacion(this); }
    }
}