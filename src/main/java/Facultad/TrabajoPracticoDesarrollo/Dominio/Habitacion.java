package Facultad.TrabajoPracticoDesarrollo.Dominio;

import Facultad.TrabajoPracticoDesarrollo.enums.EstadoHabitacion;
import Facultad.TrabajoPracticoDesarrollo.enums.TipoHabitacion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "habitacion")
@Getter @Setter
public class Habitacion {

    @Id
    @Column(name = "numero") // Clave primaria String
    private String numero;

    @Column(name = "tipo_habitacion")
    private TipoHabitacion tipoHabitacion;

    @Column(name = "capacidad")
    private Integer capacidad;

    @Column(name = "estado_habitacion")
    private EstadoHabitacion estadoHabitacion;

    @Column(name = "costo_por_noche")
    private Float costoPorNoche;

    // --- RELACIÓN CON CAMAS (1 a N) ---
    // mappedBy = "habitacion" significa que la clase Cama es la dueña de la relación (tiene la FK)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "habitacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Cama> camas = new ArrayList<>();

    // --- RELACIÓN CON RESERVAS (1 a N) ---
    // Para ver todas las reservas de esta habitación.
    // Usamos JsonIgnore para no hacer bucle infinito
    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "habitacion")
    private List<Reserva> reservas = new ArrayList<>();

    public Habitacion() {}

    private Habitacion(Builder builder) {
        this.numero = builder.numero;
        this.tipoHabitacion = builder.tipoHabitacion;
        this.capacidad = builder.capacidad;
        this.estadoHabitacion = builder.estadoHabitacion;
        this.costoPorNoche = (float) builder.costoPorNoche;
        this.reservas = builder.reservas;
    }

    // --- BUILDER (Intacto) ---
    public static class Builder {
        private String numero;
        private TipoHabitacion tipoHabitacion;
        private Integer capacidad;
        private EstadoHabitacion estadoHabitacion;
        private float costoPorNoche;
        private ArrayList<Reserva> reservas = new ArrayList<>();

        public Builder() {}

        public Builder numero(String val) { numero = val; return this;}
        public Builder tipoHabitacion(TipoHabitacion val) { tipoHabitacion = val; return this; }
        public Builder capacidad(Integer val) { capacidad = val; return this;}
        public Builder estado(EstadoHabitacion val) { estadoHabitacion = val; return this; }
        public Builder costo(float val) { costoPorNoche = val; return this; }
        public Builder reservas(ArrayList<Reserva> val) { reservas = val; return this; }

        public Habitacion build() { return new Habitacion(this); }
    }
}