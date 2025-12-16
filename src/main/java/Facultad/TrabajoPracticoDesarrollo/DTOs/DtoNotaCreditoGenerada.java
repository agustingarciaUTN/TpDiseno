package Facultad.TrabajoPracticoDesarrollo.DTOs;

import lombok.Data;

@Data
public class DtoNotaCreditoGenerada {
    private String nroNotaCredito;
    private String responsableNombre;
    private Double importeNeto;
    private Double importeIva;
    private Double importeTotal;
}