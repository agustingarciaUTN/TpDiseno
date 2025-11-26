package Dominio;

import java.util.Date;

import enums.EstadoFactura;
import enums.TipoFactura;

public class Factura {
    private int idFactura;
    private ResponsablePago responsablePago;
    private int idResonsablePago; // CUAL DE LOS DOS
    private String numeroFactura;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private EstadoFactura estadoFactura;
    private float iva;
    private float importeNeto;
    private float importeTotal;
    private TipoFactura tipoFactura;
    private int idNotaDeCredito; // CUAL DE LOS DOS
    private NotaDeCredito notaDeCredito;
    private Estadia estadia; // CUAL DE LOS DOS
    private int idEstadia;

}
