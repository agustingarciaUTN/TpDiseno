//package Facultad.TrabajoPracticoDesarrollo.Services.Gestores;
//
//import Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs.DaoMedioDePago;
//import Facultad.TrabajoPracticoDesarrollo.TrabajoPracticoDesarrolloApplication;
//import Facultad.TrabajoPracticoDesarrollo.Repositories.DAOs.DaoPago;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class GestorPago {
//
//    // 1. La única instancia (static y private)
//    private static GestorPago instancia;
//    private final DaoPago daoPago;
//    private final DaoMedioDePago daoMedioDePago;
//    private final TrabajoPracticoDesarrolloApplication.DaoPersonaJuridica daoPersonaJuridica;
//    private final TrabajoPracticoDesarrolloApplication.DaoPersonaFisica daoPersonaFisica;
//
//    // 2. Constructor PRIVADO
//    // Nadie puede hacer new GestorPago() desde fuera.
//    @Autowired
//    private GestorPago(DaoPago daoPago, DaoMedioDePago daoMedioDePago, TrabajoPracticoDesarrolloApplication.DaoPersonaJuridica daoPersonaJuridica, TrabajoPracticoDesarrolloApplication.DaoPersonaFisica daoPersonaFisica) {
//        // Obtenemos las instancias de los DAOs
//        // Referencias a los DAOs que este gestor necesita
//        this.daoPago = daoPago;
//        this.daoMedioDePago = daoMedioDePago;
//        this.daoPersonaJuridica = daoPersonaJuridica;
//        this.daoPersonaFisica = daoPersonaFisica;
//    }
//
//
//}
