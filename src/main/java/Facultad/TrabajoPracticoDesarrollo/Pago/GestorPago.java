package Facultad.TrabajoPracticoDesarrollo.Pago;

import Facultad.TrabajoPracticoDesarrollo.MedioDePago.DaoInterfazMedioDePago;
import Facultad.TrabajoPracticoDesarrollo.MedioDePago.DaoMedioDePago;
import Facultad.TrabajoPracticoDesarrollo.ResponsablePago.DaoInterfazPersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.ResponsablePago.DaoInterfazPersonaJuridica;
import Facultad.TrabajoPracticoDesarrollo.ResponsablePago.DaoPersonaFisica;
import Facultad.TrabajoPracticoDesarrollo.ResponsablePago.DaoPersonaJuridica;

public class GestorPago {

    // 1. La única instancia (static y private)
    private static GestorPago instancia;

    // 2. Constructor PRIVADO
    // Nadie puede hacer new GestorPago() desde fuera.
    private GestorPago() {
        // Obtenemos las instancias de los DAOs
        // Referencias a los DAOs que este gestor necesita
        DaoInterfazPago daoPago = DaoPago.getInstance();
        DaoInterfazMedioDePago daoMedioDePago = DaoMedioDePago.getInstance();
        DaoInterfazPersonaFisica daoPersonaFisica = DaoPersonaFisica.getInstance();
        DaoInterfazPersonaJuridica daoPersonaJuridica = DaoPersonaJuridica.getInstance();
    }

    // 3. Método de Acceso Global (Synchronized para seguridad en hilos)
    public static synchronized GestorPago getInstance() {
        if (instancia == null) {
            instancia = new GestorPago();
        }
        return instancia;
    }
}
