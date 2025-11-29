package Huesped;

import Dominio.Estadia;
import Estadia.DtoEstadia;
import enums.TipoDocumento;
import enums.PosIva;
import java.util.Date;
import java.util.List;

public class DtoHuesped {
    // Atributos (igual que antes)
    private String nombres;
    private String apellido;
    private long telefono;
    private TipoDocumento tipoDocumento;
    private String documento; // Recuerda que en DTO es String
    private String cuit;
    private String posicionIva; // String en DTO
    private Date fechaNacimiento;
    private String email;
    private String ocupacion;
    private String nacionalidad;
    private DtoDireccion dtoDireccion;
    private List<DtoEstadia> dtoEstadias;

    // 1. Constructor Privado (Recibe el Builder)
    private DtoHuesped(Builder builder) {
        this.nombres = builder.nombres;
        this.apellido = builder.apellido;
        this.telefono = builder.telefono;
        this.tipoDocumento = builder.tipoDocumento;
        this.documento = builder.documento;
        this.cuit = builder.cuit;
        this.posicionIva = builder.posicionIva;
        this.fechaNacimiento = builder.fechaNacimiento;
        this.email = builder.email;
        this.ocupacion = builder.ocupacion;
        this.nacionalidad = builder.nacionalidad;
        this.dtoDireccion = builder.dtoDireccion;
        this.dtoEstadias = builder.dtoEstadias;
    }

    // Constructor vacío (necesario a veces para frameworks o serialización)
    public DtoHuesped() {}

    // Getters y Setters... (Mantenlos todos igual)

    // 2. Clase Static Builder
    public static class Builder {
        // Mismos atributos que la clase externa
        private String nombres;
        private String apellido;
        private TipoDocumento tipoDocumento;
        private String documento;

        // Opcionales inicializados
        private long telefono;
        private String cuit;
        private String posicionIva;
        private Date fechaNacimiento;
        private String email;
        private String ocupacion;
        private String nacionalidad;
        private DtoDireccion dtoDireccion;
        private List<DtoEstadia> dtoEstadias;

        // Constructor del Builder (puedes pedir datos mínimos obligatorios o dejarlo vacío)
        public Builder() {}

        public Builder nombres(String val) { nombres = val; return this; }
        public Builder apellido(String val) { apellido = val; return this; }
        public Builder telefono(long val) { telefono = val; return this; }
        public Builder tipoDocumento(TipoDocumento val) { tipoDocumento = val; return this; }
        public Builder documento(String val) { documento = val; return this; }
        public Builder cuit(String val) { cuit = val; return this; }
        public Builder posicionIva(String val) { posicionIva = val; return this; }
        public Builder fechaNacimiento(Date val) { fechaNacimiento = val; return this; }
        public Builder email(String val) { email = val; return this; }
        public Builder ocupacion(String val) { ocupacion = val; return this; }
        public Builder nacionalidad(String val) { nacionalidad = val; return this; }
        public Builder direccion(DtoDireccion val) { dtoDireccion = val; return this; }
        public Builder estadias(List<DtoEstadia> val) { dtoEstadias = val; return this; }

        public DtoHuesped build() {
            return new DtoHuesped(this);
        }
    }
}