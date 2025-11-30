package Factura;

public class GestorFacturas {

    // 1. La única instancia (static y private)
    private static GestorFacturas instancia;

    // Referencias a los DAOs que necesita
    private final DaoInterfazNotaDeCredito daoNotaDeCredito;
    private final DaoInterfazFactura daoFactura;

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorFacturas() desde fuera.
    private GestorFacturas() {
        //Obtenemos las instancias de los DAOs
        this.daoNotaDeCredito = DaoNotaDeCredito.getInstance();
        this.daoFactura = DaoFactura.getInstance();
    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorFacturas getInstance() {
        if (instancia == null) {
            instancia = new GestorFacturas();
        }
        return instancia;
    }
}
