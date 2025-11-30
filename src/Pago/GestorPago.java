package Pago;

import MedioDePago.DaoInterfazMedioDePago;
import MedioDePago.DaoMedioDePago;
import ResponsablePago.DaoInterfazPersonaFisica;
import ResponsablePago.DaoInterfazPersonaJuridica;
import ResponsablePago.DaoPersonaFisica;
import ResponsablePago.DaoPersonaJuridica;

public class GestorPago {

    // 1. La única instancia (static y private)
    private static GestorPago instancia;

    // Referencias a los DAOs que este gestor necesita
    private final DaoInterfazPago daoPago;
    private final DaoInterfazMedioDePago daoMedioDePago;
    private final DaoInterfazPersonaJuridica daoPersonaJuridica;
    private final DaoInterfazPersonaFisica daoPersonaFisica;

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorPago() desde fuera.
    private GestorPago() {
        // Obtenemos las instancias de los DAOs
        this.daoPago = DaoPago.getInstance();
        this.daoMedioDePago = DaoMedioDePago.getInstance();
        this.daoPersonaFisica = DaoPersonaFisica.getInstance();
        this.daoPersonaJuridica = DaoPersonaJuridica.getInstance();
    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorPago getInstance() {
        if (instancia == null) {
            instancia = new GestorPago();
        }
        return instancia;
    }
}
