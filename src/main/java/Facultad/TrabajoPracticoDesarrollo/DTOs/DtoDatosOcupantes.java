package Facultad.TrabajoPracticoDesarrollo.DTOs;

import Facultad.TrabajoPracticoDesarrollo.enums.TipoDocumento;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoDatosOcupantes {
    // Atributos (Solo los necesarios para filtrar)

    @NotBlank
    private String apellido;

    @NotBlank
    private String nombres;

    private TipoDocumento tipoDocumento;

    private String nroDocumento;

    @AssertTrue(message = "El formato del número de documento no coincide con el tipo seleccionado")
    public boolean isDocumentoValido() {
        //Si no escribió número, es válido
        if (nroDocumento == null || nroDocumento.trim().isEmpty()) {
            return true;
        }

        //Si escribió número pero NO eligió tipo, validamos genérico
        if (tipoDocumento == null) {
            boolean esNumero = nroDocumento.matches("^\\d{7,8}$");       // Parece DNI
            boolean esPasaporte = nroDocumento.matches("^[A-Z0-9]{6,15}$"); // Parece Pasaporte
            return esNumero || esPasaporte;
        }

        //Si eligió tipo, validamos el formato específico
        switch (tipoDocumento) {
            case DNI:
            case LE:
            case LC:
                // Solo números, 7 u 8 dígitos (según tu lógica en Pantalla.java)
                return nroDocumento.matches("^\\d{7,8}$");

            case PASAPORTE:
                // Letras y números
                return nroDocumento.matches("^[A-Z0-9]{6,15}$");

            default: // OTRO
                return nroDocumento.matches("^.{4,20}$");
        }
    }

    // --- CONSTRUCTOR PRIVADO ---
    private DtoDatosOcupantes(Builder builder) {
        this.apellido = builder.apellido;
        this.nombres = builder.nombres;
        this.tipoDocumento = builder.tipoDocumento;
        this.nroDocumento = builder.nroDocumento;
    }

    public DtoDatosOcupantes() {}

    // --- GETTERS Y SETTERS ---
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNroDocumento() { return nroDocumento; }
    public void setNroDocumento(String nroDocumento) { this.nroDocumento = nroDocumento; }

    // --- CLASE STATIC BUILDER ---
    public static class Builder {
        private String apellido;
        private String nombres;
        private TipoDocumento tipoDocumento;
        private String nroDocumento;

        public Builder() {}

        public Builder apellido(String val) { apellido = val; return this; }
        public Builder nombres(String val) { nombres = val; return this; }
        public Builder tipoDocumento(TipoDocumento val) { tipoDocumento = val; return this; }
        public Builder nroDocumento(String val) { nroDocumento = val; return this; }

        public DtoDatosOcupantes build() {
            return new DtoDatosOcupantes(this);
        }
    }

    public boolean estanVacios() {
        boolean apVacio = (apellido == null || apellido.trim().isEmpty());
        boolean nomVacio = (nombres == null || nombres.trim().isEmpty());
        boolean tipoVacio = (tipoDocumento == null);
        boolean docVacio = (nroDocumento == null || nroDocumento.trim().isEmpty() || nroDocumento.equals("0"));

        return apVacio && nomVacio && tipoVacio && docVacio;
    }
}