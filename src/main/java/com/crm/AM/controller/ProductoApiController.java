package com.crm.AM.controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.AM.entities.Producto;
import com.crm.AM.repository.DetalleFacturaRespository;
import com.crm.AM.repository.ProductoRepository;

@RestController //Indicamos a spring que va a ser un controlador
@RequestMapping("/api/productos") //Indicamos la ruta base para las peticiones
public class ProductoApiController {

    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private DetalleFacturaRespository detalleFacturaRespository;

    // Agregar producto
    @PostMapping("/agregar")
    public String agregarProducto(@RequestBody Producto producto) {
        producto.setActivo(true);
        productoRepository.save(producto);
        return "Producto agregado con éxito.";
    }

    // Eliminar producto por ID
    @DeleteMapping("/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoRepository.deleteById(id);
        return "Producto eliminado con éxito.";
    }

    // Buscar producto por nombre
    @GetMapping("/buscar")
    public List<Producto> buscarProducto(@RequestParam String nombre) {
        return productoRepository.findByNombre(nombre);
    }

    //Listar activos 
    @GetMapping("/activos")
    public List<Producto> listarProductosActivos() {
        return productoRepository.findByActivoTrue();
    }

    // Listar todos los productos
    @GetMapping
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // Actualizar producto por ID
    @PutMapping("/actualizar/{id}")
    public String actualizarProducto(@PathVariable Long id, @RequestBody Producto datos) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "Producto no encontrado";
        }

        if (datos.getNombre() != null) {
            producto.setNombre(datos.getNombre());
        }
        if (datos.getDescripcion() != null) {
            producto.setDescripcion(datos.getDescripcion());
        }
        if (datos.getPrecio() != null) {
            producto.setPrecio(datos.getPrecio());
        }
        if (datos.getStock() != null) {
            producto.setStock(datos.getStock());
        }
        if (datos.getActivo() != null) {
            producto.setActivo(datos.getActivo());
        }

        productoRepository.save(producto);
        return "Producto actualizado correctamente";
    }

    // Gestionar inventario (agregar o remover stock) 
    //controler que alfinal tampoco se usa
    @PutMapping("/inventario/{id}")
    public String gestionarInventario(@PathVariable Long id, @RequestParam int cantidad, @RequestParam boolean agregar) {
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto == null) {
            return "Producto no encontrado";
        }

        if (agregar) {
            producto.setStock(producto.getStock() + cantidad);
        } else {
            if (producto.getStock() < cantidad) {
                return "Stock insuficiente";
            }
            producto.setStock(producto.getStock() - cantidad);
        }
        productoRepository.save(producto);
        return "Inventario actualizado con éxito.";
    }

    // Filtrar productos por rango de precio
    @GetMapping("/filtrar/precio")
    public List<Producto> filtrarPorPrecio(@RequestParam double min, @RequestParam double max) {
        return productoRepository.findByPrecioBetween(min, max);
    }

    // Ver productos más vendidos 
    //controler que alfinal no llego a utilizar
    @GetMapping("/mas-vendidos")
    public List<Object[]> productosMasVendidos(@RequestParam boolean top) {
        List<Object[]> ranking = detalleFacturaRespository.findRankingProductosVendidos();
        if (!top) {
            Collections.reverse(ranking);
        }
        return ranking.subList(0, Math.min(5, ranking.size()));
    }

    @GetMapping("/conteo")
    public Map<String, Object> contarProductos() {
        long total = productoRepository.count();
        long activos = productoRepository.countByActivoTrue();
        long bajoStock = productoRepository.countByStockLessThan(10);
        double crecimiento = total > 0 ? ((double) (total - bajoStock) / total) * 100 : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("activos", activos);
        result.put("bajoStock", bajoStock);
        result.put("crecimiento", crecimiento);

        return result;
    }

    // Ver productos con baja existencia
    @GetMapping("/baja-existencia")
    public List<Producto> productosBajaExistencia() {
        List<Producto> productos = productoRepository.findAll();
        productos.sort(Comparator.comparing(Producto::getStock));
        return productos;
    }
}
