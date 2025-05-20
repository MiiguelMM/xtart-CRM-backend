package com.crm.AM.controller;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/facturas")
public class FacturaApiController {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private DetalleFacturaRespository detalleFacturaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    // Realizar nueva venta
    @PostMapping("/nueva")
    public String realizarNuevaVenta(
            @RequestParam Long clienteId,
            @RequestParam Long empleadoId,
            @RequestParam List<Long> productoIds,
            @RequestParam List<Integer> cantidades) {

        if (productoIds.size() != cantidades.size()) {
            return "Cantidad de productos y cantidades no coinciden.";
        }

        Cliente cliente = clienteRepository.findById(clienteId).orElse(null);
        Empleado empleado = empleadoRepository.findById(empleadoId).orElse(null);
        if (cliente == null || empleado == null) {
            return "Cliente o empleado no encontrado.";
        }

        List<DetalleFactura> detalles = new ArrayList<>();
        double totalVenta = 0.0;
        List<Producto> productosActualizados = new ArrayList<>();

        for (int i = 0; i < productoIds.size(); i++) {
            Producto producto = productoRepository.findById(productoIds.get(i)).orElse(null);
            if (producto == null) {
                return "Producto no encontrado: ID " + productoIds.get(i);
            }

            int cantidad = cantidades.get(i);

            // Verificar stock disponible
            if (producto.getStock() < cantidad) {
                return "Stock insuficiente para el producto: " + producto.getNombre()
                        + ". Disponible: " + producto.getStock() + ", Solicitado: " + cantidad;
            }

            // Actualizar stock
            producto.setStock(producto.getStock() - cantidad);
            productosActualizados.add(producto);

            double subtotal = producto.getPrecio() * cantidad;
            totalVenta += subtotal;

            DetalleFactura detalle = new DetalleFactura(null, null, producto, cantidad, producto.getPrecio(), subtotal);
            detalles.add(detalle);
        }

        // Guardar venta
        Factura factura = new Factura(null, cliente, empleado, null, totalVenta, detalles);
        facturaRepository.save(factura);

        for (DetalleFactura detalle : detalles) {
            detalle.setFactura(factura);
            detalleFacturaRepository.save(detalle);
        }

        // Guardar productos con stock actualizado
        productoRepository.saveAll(productosActualizados);

        return "Venta realizada con éxito. Total: " + totalVenta;
    }

    // Buscar venta por ID
    @GetMapping("/{id}")
    public Map<String, Object> buscarVenta(@PathVariable Long id) {
        Factura venta = facturaRepository.findById(id).orElse(null);
        if (venta == null) {
            return null;
        }

        Map<String, Object> datos = new HashMap<>();
        datos.put("id", venta.getId());
        datos.put("cliente", venta.getCliente().getNombre());
        datos.put("empleado", venta.getEmpleado() != null ? venta.getEmpleado().getNombre() : "No asignado");
        datos.put("total", venta.getTotal());
        datos.put("fecha", venta.getFecha());
        return datos;
    }

    // Listar todas las ventas
    @GetMapping
    public List<Map<String, Object>> listarVentas() {
        List<Factura> ventas = facturaRepository.findAll();
        List<Map<String, Object>> respuesta = new ArrayList<>();

        for (Factura venta : ventas) {
            Map<String, Object> datos = new HashMap<>();
            datos.put("id", venta.getId());
            datos.put("cliente", venta.getCliente().getNombre());
            datos.put("empleado", venta.getEmpleado() != null ? venta.getEmpleado().getNombre() : "No asignado");
            datos.put("total", venta.getTotal());
            datos.put("fecha", venta.getFecha());
            respuesta.add(datos);
        }
        return respuesta;
    }

    // Generar reporte de ventas
    @GetMapping("/reporte")
    public String generarReporteVentas() {
        return "Reporte de ventas generado (lógica aún no implementada).";
    }

    // Buscar ventas por cliente ID
    @GetMapping("/cliente/{clienteId}")
    public List<Factura> buscarVentasPorCliente(@PathVariable Long clienteId) {
        return facturaRepository.findByClienteId(clienteId);
    }

    // Ver detalle de una venta
    @GetMapping("/detalle/{facturaId}")
    public Map<String, Object> verDetalleVenta(@PathVariable Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId).orElse(null);
        if (factura == null) {
            return Collections.singletonMap("error", "Venta no encontrada.");
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("id", factura.getId());
        respuesta.put("fecha", factura.getFecha());
        respuesta.put("total", factura.getTotal());
        respuesta.put("clienteId", factura.getCliente().getId());
        respuesta.put("cliente", factura.getCliente().getNombre());

        if (factura.getEmpleado() != null) {
            respuesta.put("empleadoId", factura.getEmpleado().getId());
            respuesta.put("empleado", factura.getEmpleado().getNombre());
        } else {
            respuesta.put("empleado", "No asignado");
        }

        List<Map<String, Object>> detalles = new ArrayList<>();
        for (DetalleFactura d : factura.getDetalles()) {
            Map<String, Object> item = new HashMap<>();
            item.put("productoId", d.getProducto().getId());
            item.put("producto", d.getProducto().getNombre());
            item.put("precioUnitario", d.getPrecioUnitario());
            item.put("cantidad", d.getCantidad());
            item.put("subtotal", d.getSubtotal());
            detalles.add(item);
        }
        respuesta.put("detalles", detalles);

        return respuesta;
    }

    // Aplicar descuento a una venta
    @PutMapping("/descuento/{facturaId}")
    public String aplicarDescuento(
            @PathVariable Long facturaId,
            @RequestParam double porcentaje) {

        Factura factura = facturaRepository.findById(facturaId).orElse(null);
        if (factura == null) {
            return "Factura no encontrada.";
        }

        double descuento = factura.getTotal() * (porcentaje / 100);
        factura.setTotal(factura.getTotal() - descuento);
        facturaRepository.save(factura);
        return "Descuento aplicado. Nuevo total: " + factura.getTotal();
    }

    // Gestionar devoluciones
    @PostMapping("/devolucion")
    public String gestionarDevoluciones() {
        return "Devolución gestionada (falta lógica).";
    }

    // Ver ventas por producto
    @GetMapping("/producto")
    public List<Map<String, Object>> verVentasPorProducto() {
        List<Object[]> resultados = detalleFacturaRepository.findVentasPorProducto();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Object[] fila : resultados) {
            Producto producto = (Producto) fila[0];
            Long totalVendidas = (Long) fila[1];
            Map<String, Object> item = new HashMap<>();
            item.put("producto", producto.getNombre());
            item.put("unidadesVendidas", totalVendidas);
            lista.add(item);
        }
        return lista;
    }

    // Ver ventas por vendedor
    @GetMapping("/vendedor")
    public List<Map<String, Object>> verVentasPorVendedor() {
        List<Object[]> resultados = facturaRepository.findVentasPorVendedor();
        List<Map<String, Object>> lista = new ArrayList<>();

        for (Object[] fila : resultados) {
            Empleado empleado = (Empleado) fila[0];
            Long totalVentas = (Long) fila[1];
            Map<String, Object> item = new HashMap<>();
            item.put("vendedor", empleado.getNombre() + " " + empleado.getApellido());
            item.put("ventas", totalVentas);
            lista.add(item);
        }
        return lista;
    }

    // Exportar ventas
    @GetMapping("/exportar")
    public String exportarVentas() {
        return "Ventas exportadas (falta lógica).";
    }

    @GetMapping("/pdf/{id}")
    public void generarPdf(@PathVariable Long id, HttpServletResponse response) {
        try {
            // Buscar la factura
            Factura factura = facturaRepository.findById(id).orElse(null);
            if (factura == null) {
                response.sendError(404, "Factura no encontrada");
                return;
            }

            // Crear PDF en memoria primero
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // --- TÍTULO Y ENCABEZADO ---
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD);
            Paragraph empresa = new Paragraph("Xtart Solutions", titleFont);
            empresa.setAlignment(Element.ALIGN_CENTER);
            document.add(empresa);
            document.add(new Paragraph(" "));

            // Línea separadora
            LineSeparator line = new LineSeparator();
            document.add(line);
            document.add(new Paragraph(" "));

            // --- INFORMACIÓN BÁSICA ---
            String fechaStr = factura.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            document.add(new Paragraph("Fecha: " + fechaStr));

            String nombreCliente = factura.getCliente().getNombre() + " "
                    + (factura.getCliente().getApellido() != null
                    ? factura.getCliente().getApellido() : "");
            document.add(new Paragraph("Cliente: " + nombreCliente));

            String vendedor = factura.getEmpleado() != null
                    ? factura.getEmpleado().getNombre() + " "
                    + factura.getEmpleado().getApellido() : "No asignado";
            document.add(new Paragraph("Vendedor: " + vendedor));

            document.add(new Paragraph(" "));

            // --- TABLA DE PRODUCTOS ---
            PdfPTable tabla = new PdfPTable(4);
            tabla.setWidthPercentage(100);

            // Encabezados
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

            PdfPCell cell1 = new PdfPCell(new Phrase("Producto", headerFont));
            cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell1.setPadding(5);
            tabla.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Precio", headerFont));
            cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell2.setPadding(5);
            tabla.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Cantidad", headerFont));
            cell3.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell3.setPadding(5);
            tabla.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Subtotal", headerFont));
            cell4.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell4.setPadding(5);
            tabla.addCell(cell4);

            // Datos de productos
            for (DetalleFactura detalle : factura.getDetalles()) {
                String nombre = detalle.getProducto() != null
                        ? detalle.getProducto().getNombre() : "Producto";

                tabla.addCell(nombre);
                tabla.addCell(String.format("$%.2f", detalle.getPrecioUnitario()));
                tabla.addCell(String.valueOf(detalle.getCantidad()));
                tabla.addCell(String.format("$%.2f", detalle.getSubtotal()));
            }

            document.add(tabla);
            document.add(new Paragraph(" "));

            // --- TOTAL ---
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Paragraph total = new Paragraph(String.format("TOTAL: $%.2f", factura.getTotal()), boldFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.add(new Paragraph(" "));

            // --- PIE DE PÁGINA ---
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
            Paragraph footer = new Paragraph("Gracias por su compra\nXtart Solutions", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();

            // Una vez generado correctamente, enviarlo como respuesta
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=factura-" + id + ".pdf");
            response.setContentLength(baos.size());

            // Escribir al output
            ServletOutputStream sos = response.getOutputStream();
            baos.writeTo(sos);
            sos.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
