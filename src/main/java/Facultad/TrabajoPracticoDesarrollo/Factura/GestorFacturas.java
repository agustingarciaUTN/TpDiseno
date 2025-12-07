package Facultad.TrabajoPracticoDesarrollo.Factura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GestorFacturas {

    // 1. La única instancia (static y private)
    private static GestorFacturas instancia;
    private final DaoFactura daoFactura;
    private final DaoNotaDeCredito daoNotaDeCredito;

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorFacturas() desde fuera.
    @Autowired
    private GestorFacturas(DaoFactura daoFactura, DaoNotaDeCredito daoNotaDeCredito) {
        //Obtenemos las instancias de los DAOs
        // Referencias a los DAOs que necesita
        this.daoNotaDeCredito = daoNotaDeCredito;
        this.daoFactura = daoFactura;
    }


}
