package com.crm.AM.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.AM.entities.DetalleFactura;
import com.crm.AM.entities.Factura;
import com.crm.AM.entities.Producto;
import com.crm.AM.repository.DetalleFacturaRespository;
import com.crm.AM.repository.FacturaRepository;
import com.crm.AM.repository.ProductoRepository;

@RestController
@RequestMapping("/api/detalle-factura")
public class DetalleFacturaApiController {

    @Autowired
    private DetalleFacturaRespository detalleFacturaRespository;

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Crear un nuevo detalle de factura
    @PostMapping
    public String crearDetalleFactura(@RequestParam Long facturaId,
                                      @RequestParam Long productoId,
                                      @RequestParam int cantidad) {
        Factura factura = facturaRepository.findById(facturaId).orElse(null);
        Producto producto = productoRepository.findById(productoId).orElse(null);

        if (factura == null || producto == null) {
            return "Factura o Producto no encontrado.";
        }

        DetalleFactura detalle = new DetalleFactura();
        detalle.setFactura(factura);
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(producto.getPrecio()); 

        detalleFacturaRespository.save(detalle);
        return "Detalle de factura creado con éxito.";
    }

    // Obtener todos los detalles de una factura
    @GetMapping("/{facturaId}")
    public Optional<DetalleFactura> obtenerDetallesPorFactura(@PathVariable Long facturaId) {
        return detalleFacturaRespository.findById(facturaId);
    }

    // Editar un detalle de factura
    @PutMapping("/{id}")
    public String editarDetalleFactura(@PathVariable Long id,
                                       @RequestParam(required = false) Integer nuevaCantidad,
                                       @RequestParam(required = false) Double nuevoPrecioUnitario) {
        DetalleFactura detalle = detalleFacturaRespository.findById(id).orElse(null);
        if (detalle == null) {
            return "Detalle no encontrado.";
        }

        if (nuevaCantidad != null) {
            detalle.setCantidad(nuevaCantidad);
        }

        if (nuevoPrecioUnitario != null) {
            detalle.setPrecioUnitario(nuevoPrecioUnitario);
        }

        detalleFacturaRespository.save(detalle);
        return "Detalle actualizado.";
    }

    // Eliminar un detalle de factura
    @DeleteMapping("/{id}")
    public String eliminarDetalleFactura(@PathVariable Long id) {
        if (!detalleFacturaRespository.existsById(id)) {
            return "Detalle no encontrado.";
        }

        detalleFacturaRespository.deleteById(id);
        return "Detalle eliminado.";
    }

    //  Listar todos los detalles 
    @GetMapping
    public List<DetalleFactura> listarTodosLosDetalles() {
        return detalleFacturaRespository.findAll();
    }
}