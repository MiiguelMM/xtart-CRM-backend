package com.crm.AM.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.AM.entities.Cliente;
import com.crm.AM.entities.DetalleFactura;
import com.crm.AM.entities.Empleado;
import com.crm.AM.entities.Factura;
import com.crm.AM.entities.Producto;
import com.crm.AM.repository.ClienteRepository;
import com.crm.AM.repository.DetalleFacturaRespository;
import com.crm.AM.repository.EmpleadoRepository;
import com.crm.AM.repository.FacturaRepository;
import com.crm.AM.repository.ProductoRepository;

@RestController
@RequestMapping("/api/datos")
public class DatosController {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private DetalleFacturaRespository detalleFacturaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @PostMapping("/reset")
    public String resetDatos() {
        try {
            // 1. Eliminar datos en orden para respetar integridad referencial
            detalleFacturaRepository.deleteAll();
            facturaRepository.deleteAll();
            clienteRepository.deleteAll();
            productoRepository.deleteAll();
            empleadoRepository.deleteAll();

            // 2. Crear nuevos datos de prueba
            crearDatosDePrueba();

            return "Datos restablecidos correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al restablecer datos: " + e.getMessage();
        }
    }

    private void crearDatosDePrueba() {
        // Crear empleados
        List<Empleado> empleados = new ArrayList<>();

        empleados.add(crearEmpleado("Ana", "García", "ana.garcia@xtart.com", "611223344", "Ventas", "password123", true));
        empleados.add(crearEmpleado("Carlos", "López", "carlos.lopez@xtart.com", "622334455", "Administración", "password123", true));
        empleados.add(crearEmpleado("María", "Rodríguez", "maria.rodriguez@xtart.com", "633445566", "Ventas", "password123", true));
        empleados.add(crearEmpleado("Juan", "Martínez", "juan.martinez@xtart.com", "644556677", "Soporte", "password123", true));
        empleados.add(crearEmpleado("Laura", "Sánchez", "laura.sanchez@xtart.com", "655667788", "Desarrollo", "password123", true));
        empleados.add(crearEmpleado("David", "Pérez", "david.perez@xtart.com", "666778899", "Ventas", "password123", true));

        empleadoRepository.saveAll(empleados);

        // Crear clientes
        List<Cliente> clientes = new ArrayList<>();

        clientes.add(crearCliente("Antonio", "Hernández", "antonio.hernandez@email.com", "911223344", "Calle Mayor 1, Madrid", "client123", true));
        clientes.add(crearCliente("Elena", "Gómez", "elena.gomez@email.com", "922334455", "Avenida Libertad 23, Barcelona", "client123", true));
        clientes.add(crearCliente("Miguel", "Torres", "miguel.torres@email.com", "933445566", "Plaza España 4, Sevilla", "client123", true));
        clientes.add(crearCliente("Sofía", "Navarro", "sofia.navarro@email.com", "944556677", "Calle San Juan 15, Valencia", "client123", true));
        clientes.add(crearCliente("Pablo", "Jiménez", "pablo.jimenez@email.com", "955667788", "Avenida Principal 42, Málaga", "client123", true));
        clientes.add(crearCliente("Lucía", "Ruiz", "lucia.ruiz@email.com", "966778899", "Calle Nueva 7, Bilbao", "client123", true));
        clientes.add(crearCliente("Alberto", "Serrano", "alberto.serrano@email.com", "977889900", "Avenida del Puerto 12, Alicante", "client123", true));
        clientes.add(crearCliente("Carmen", "Molina", "carmen.molina@email.com", "988990011", "Calle Ancha 9, Zaragoza", "client123", true));
        clientes.add(crearCliente("Javier", "Ortega", "javier.ortega@email.com", "999001122", "Plaza Mayor 3, Murcia", "client123", true));

        clienteRepository.saveAll(clientes);

        // Crear productos
        List<Producto> productos = new ArrayList<>();

        productos.add(crearProducto("iPhone 15", "Smartphone, 128GB, pantalla 6.1\", cámara dual 48MP", 899.99, 15, true));
        productos.add(crearProducto("Samsung Galaxy S24", "Smartphone, 256GB, pantalla 6.2\", cámara 50MP", 849.99, 12, true));
        productos.add(crearProducto("Portátil HP Pavilion", "Intel i5, 16GB RAM, 512GB SSD, pantalla 15.6\"", 749.99, 10, true));
        productos.add(crearProducto("Televisor Samsung 4K", "Smart TV 55\", UHD 4K, HDR10+", 599.99, 8, true));
        productos.add(crearProducto("Tablet Samsung Galaxy Tab", "10.5\", 128GB, WiFi, batería 7040mAh", 329.99, 20, true));
        productos.add(crearProducto("Smartwatch Apple Watch SE", "GPS, detección caídas, monitor cardíaco", 249.99, 18, true));
        productos.add(crearProducto("Auriculares Sony WH-1000XM5", "Cancelación de ruido, Bluetooth 5.2, 30h batería", 379.99, 25, true));
        productos.add(crearProducto("Altavoz Inteligente Amazon Echo", "Con Alexa, sonido premium, control del hogar", 99.99, 30, true));

        productoRepository.saveAll(productos);

        // Crear facturas y detalles
        Random random = new Random();

        // Últimos 30 días - generando datos para gráficas
        for (int i = 30; i > 0; i -= 2) {
            Cliente cliente = clientes.get(random.nextInt(clientes.size()));
            Empleado empleado = empleados.get(random.nextInt(empleados.size()));

            // Seleccionar 1-3 productos aleatorios para esta factura
            List<Producto> productosFactura = seleccionarProductosAleatorios(productos, random.nextInt(3) + 1);

            crearFacturaConDetalles(cliente, empleado, productosFactura, LocalDateTime.now().minusDays(i));
        }
    }

    private Cliente crearCliente(String nombre, String apellido, String email, String telefono, String direccion, String password, Boolean activo) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        cliente.setPassword(password);
        cliente.setActivo(activo);
        return cliente;
    }

    private Empleado crearEmpleado(String nombre, String apellido, String email, String telefono, String rol, String password, Boolean activo) {
        Empleado empleado = new Empleado();
        empleado.setNombre(nombre);
        empleado.setApellido(apellido);
        empleado.setEmail(email);
        empleado.setTelefono(telefono);
        empleado.setRol(rol);
        empleado.setPassword(password);
        empleado.setActivo(activo);
        return empleado;
    }

    private Producto crearProducto(String nombre, String descripcion, Double precio, Integer stock, Boolean activo) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setActivo(activo);
        return producto;
    }

    private List<Producto> seleccionarProductosAleatorios(List<Producto> productos, int cantidad) {
        //crea un copia
        List<Producto> resultado = new ArrayList<>(productos);
        //Randomiza todo
        Collections.shuffle(resultado);
        return resultado.subList(0, Math.min(cantidad, resultado.size()));
    }

    private void crearFacturaConDetalles(Cliente cliente, Empleado empleado, List<Producto> productos, LocalDateTime fecha) {
        // 1. Crear la factura
        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setEmpleado(empleado);
        factura.setFecha(fecha);

        // 2. Crear los detalles
        List<DetalleFactura> detalles = new ArrayList<>();

        // Calcular el total a partir de los detalles
        double total = 0.0;

        for (Producto producto : productos) {
            DetalleFactura detalle = new DetalleFactura();
            detalle.setFactura(factura);
            detalle.setProducto(producto);

            // Generar una cantidad aleatoria entre 1 y 3
            int cantidad = new Random().nextInt(3) + 1;
            detalle.setCantidad(cantidad);

            detalle.setPrecioUnitario(producto.getPrecio());
            double subtotal = cantidad * producto.getPrecio();
            detalle.setSubtotal(subtotal);

            detalles.add(detalle);
            total += subtotal;
        }

        // Establecer el total y guardar la factura
        factura.setTotal(total);
        facturaRepository.save(factura);

        // Guardar los detalles
        detalleFacturaRepository.saveAll(detalles);
    }
}
