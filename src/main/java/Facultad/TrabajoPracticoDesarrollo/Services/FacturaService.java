package Facultad.TrabajoPracticoDesarrollo.Services;

import Facultad.TrabajoPracticoDesarrollo.Dominio.Factura;
import Facultad.TrabajoPracticoDesarrollo.Dominio.NotaDeCredito;
import Facultad.TrabajoPracticoDesarrollo.Repositories.FacturaRepository;
import Facultad.TrabajoPracticoDesarrollo.Repositories.NotaDeCreditoRepository;
import Facultad.TrabajoPracticoDesarrollo.enums.EstadoFactura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final NotaDeCreditoRepository notaDeCreditoRepository;

    @Autowired
    public FacturaService(FacturaRepository facturaRepository, NotaDeCreditoRepository notaDeCreditoRepository) {
        this.facturaRepository = facturaRepository;
        this.notaDeCreditoRepository = notaDeCreditoRepository;
    }

    // --- MÉTODOS DE BÚSQUEDA ---

    @Transactional(readOnly = true)
    public Factura buscarPorNumero(String numeroFactura) {
        return facturaRepository.findById(numeroFactura).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existeFactura(String numeroFactura) {
        return facturaRepository.existsById(numeroFactura);
    }

    // --- MÉTODOS TRANSACCIONALES ---

    @Transactional
    public void guardarFactura(Factura factura) throws Exception {
        if (factura == null) throw new IllegalArgumentException("La factura no puede ser nula.");

        // Validación simple: Si ya existe, no la sobreescribimos (salvo que sea lógica de update)
        if (existeFactura(factura.getNumeroFactura())) {
            throw new Exception("Ya existe una factura con el número: " + factura.getNumeroFactura());
        }

        // Si la factura tiene una Nota de Crédito asociada, JPA la guardará por cascada
        // si está configurado CascadeType.ALL en la entidad Factura.
        // Si no, deberíamos guardarla aquí:
        if (factura.getNotaDeCredito() != null) {
            notaDeCreditoRepository.save(factura.getNotaDeCredito());
        }

        facturaRepository.save(factura);
    }

    @Transactional
    public void actualizarEstado(String numeroFactura, EstadoFactura nuevoEstado) throws Exception {
        Factura factura = facturaRepository.findById(numeroFactura)
                .orElseThrow(() -> new Exception("No se encontró la factura " + numeroFactura));

        factura.setEstadoFactura(nuevoEstado);
        facturaRepository.save(factura);
    }

    @Transactional
    public void generarNotaDeCredito(NotaDeCredito nota) {
        // Lógica específica para notas de crédito
        notaDeCreditoRepository.save(nota);
    }
}