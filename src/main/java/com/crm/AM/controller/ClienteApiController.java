package com.crm.AM.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.crm.AM.entities.Cliente;
import com.crm.AM.repository.ClienteRepository;
import com.crm.AM.repository.FacturaRepository;
import com.crm.AM.service.EmailService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteApiController {
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private EmailService emailService;

    // 1. Agregar cliente
    @PostMapping("/agregar")
    public Cliente agregarCliente(@RequestBody Cliente cliente) {
        cliente.setActivo(true);
        return clienteRepository.save(cliente);
    }

    // 2. Eliminar cliente
    @DeleteMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id) {
        if (!clienteRepository.existsById(id)) {
            return "Cliente no encontrado";
        }
        clienteRepository.deleteById(id);
        return "Cliente eliminado con éxito.";
    }

    //Listar Activos 
    @GetMapping("/activos")
    public List<Cliente> listarClientesActivos() {
        return clienteRepository.findByActivoTrue();
    }

    // 3. Buscar cliente por nombre
    @GetMapping("/buscar")
    public List<Cliente> buscarCliente(@RequestParam String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // 4. Listar todos los clientes
    @GetMapping("/listar")
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // 5. Actualizar cliente
    @PutMapping("/actualizar/{id}")
    public String actualizarCliente(@PathVariable Long id, @RequestBody Cliente datosActualizados) {
        Optional<Cliente> optCliente = clienteRepository.findById(id);
        if (optCliente.isPresent()) {
            Cliente cliente = optCliente.get();

            if (datosActualizados.getNombre() != null) cliente.setNombre(datosActualizados.getNombre());
            if (datosActualizados.getEmail() != null) cliente.setEmail(datosActualizados.getEmail());
            if (datosActualizados.getTelefono() != null) cliente.setTelefono(datosActualizados.getTelefono());
            if (datosActualizados.getDireccion() != null) cliente.setDireccion(datosActualizados.getDireccion());
            if (datosActualizados.getActivo() != null) cliente.setActivo(datosActualizados.getActivo());

            clienteRepository.save(cliente);
            return "Cliente actualizado con éxito.";
        } else {
            return "Cliente no encontrado.";
        }
    }

    // 6. Ver detalles de cliente
    @GetMapping("/detalles/{id}")
    public Cliente verDetalles(@PathVariable Long id) {
        return clienteRepository.findById(id).orElse(null);
    }

    // 7. Enviar oferta
    @PostMapping("/enviar-oferta/{id}")
public ResponseEntity<String> enviarOferta(@PathVariable Long id) {
    Optional<Cliente> clienteOpt = clienteRepository.findById(id);
    String mensaje;
    String tipoMensaje;
    String iconoMensaje;
    
    if (clienteOpt.isPresent()) {
        Cliente cliente = clienteOpt.get();
        String email = cliente.getEmail();
        
        if (email == null || email.trim().isEmpty()) {
            mensaje = "El cliente " + cliente.getNombre() + " no tiene email registrado";
            tipoMensaje = "warning";
            iconoMensaje = "⚠️";
        } else {
            String asunto = "¡Oferta exclusiva para ti!";
            
            // Template de email compatible con clientes de correo
            String contenidoHtml = crearTemplateEmail(cliente.getNombre());
            
            try {
                emailService.enviarCorreoOferta(email, asunto, contenidoHtml);
                mensaje = "Correo enviado exitosamente a " + cliente.getNombre() + " (" + email + ")";
                tipoMensaje = "success";
                iconoMensaje = "✅";
            } catch (Exception e) {
                mensaje = "Error al enviar correo a " + cliente.getNombre() + ": " + e.getMessage();
                tipoMensaje = "error";
                iconoMensaje = "❌";
            }
        }
    } else {
        mensaje = "Cliente no encontrado con ID: " + id;
        tipoMensaje = "error";
        iconoMensaje = "❌";
    }

    // HTML para mostrar en el navegador (respuesta del endpoint)
    String htmlRespuesta = crearHtmlRespuesta(mensaje, tipoMensaje, iconoMensaje);
    
    return ResponseEntity.ok()
        .contentType(MediaType.TEXT_HTML)
        .body(htmlRespuesta);
}

private String crearTemplateEmail(String nombreCliente) {
    return "<!DOCTYPE html>\n" +
        "<html lang=\"es\">\n" +
        "<head>\n" +
        "    <meta charset=\"UTF-8\">\n" +
        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
        "    <title>Oferta Especial</title>\n" +
        "    <!--[if mso]>\n" +
        "    <noscript>\n" +
        "        <xml>\n" +
        "            <o:OfficeDocumentSettings>\n" +
        "                <o:PixelsPerInch>96</o:PixelsPerInch>\n" +
        "            </o:OfficeDocumentSettings>\n" +
        "        </xml>\n" +
        "    </noscript>\n" +
        "    <![endif]-->\n" +
        "</head>\n" +
        "<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;\">\n" +
        "    <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"background-color: #f4f4f4;\">\n" +
        "        <tr>\n" +
        "            <td style=\"padding: 20px 0;\">\n" +
        "                <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"600\" style=\"margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.1);\">\n" +
        "                    \n" +
        "                    <!-- Header -->\n" +
        "                    <tr>\n" +
        "                        <td style=\"background: linear-gradient(135deg, #1a4f8b 0%, #2d6cb0 100%); padding: 30px; text-align: center;\">\n" +
        "                            <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
        "                                <tr>\n" +
        "                                    <td style=\"text-align: center;\">\n" +
        "                                        <div style=\"width: 60px; height: 60px; background-color: #0a2e55; border-radius: 12px; margin: 0 auto 15px; display: inline-block; line-height: 60px; text-align: center; border: 2px solid #4fc3f7;\">\n" +
        "                                            <span style=\"color: #4fc3f7; font-size: 24px; font-weight: bold;\">🎁</span>\n" +
        "                                        </div>\n" +
        "                                        <h1 style=\"color: #1a4f8b; margin: 0; font-size: 28px; font-weight: 600; text-shadow: 0 2px 4px rgba(0,0,0,0.3);\">¡Oferta Especial!</h1>\n" +
        "                                    </td>\n" +
        "                                </tr>\n" +
        "                            </table>\n" +
        "                        </td>\n" +
        "                    </tr>\n" +
        "                    \n" +
        "                    <!-- Contenido Principal -->\n" +
        "                    <tr>\n" +
        "                        <td style=\"padding: 40px 30px;\">\n" +
        "                            <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
        "                                <tr>\n" +
        "                                    <td style=\"text-align: center;\">\n" +
        "                                        <h2 style=\"color: #1a4f8b; margin: 0 0 20px; font-size: 24px; font-weight: 600;\">Hola " + nombreCliente + ",</h2>\n" +
        "                                        <p style=\"color: #444444; font-size: 18px; line-height: 1.6; margin: 0 0 25px;\">¡Tenemos una <strong style=\"color: #1a4f8b;\">oferta exclusiva</strong> diseñada especialmente para ti!</p>\n" +
        "                                        \n" +
        "                                        <!-- Tarjeta de Oferta -->\n" +
        "                                        <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"background: linear-gradient(135deg, #e9f0f9 0%, #f8fbff 100%); border-radius: 12px; border: 2px solid #3d8ad8; margin: 20px 0;\">\n" +
        "                                            <tr>\n" +
        "                                                <td style=\"padding: 30px; text-align: center;\">\n" +
        "                                                    <div style=\"background-color: #ff6b35; color: white; display: inline-block; padding: 8px 20px; border-radius: 20px; font-size: 14px; font-weight: bold; margin-bottom: 15px;\">OFERTA LIMITADA</div>\n" +
        "                                                    <h3 style=\"color: #1a4f8b; margin: 0 0 15px; font-size: 22px; font-weight: 700;\">🚀 Descuento Especial del 25%</h3>\n" +
        "                                                    <p style=\"color: #2d6cb0; font-size: 16px; margin: 0 0 20px; line-height: 1.5;\">En todos nuestros servicios premium<br><strong>¡Solo por tiempo limitado!</strong></p>\n" +
        "                                                    <div style=\"background-color: #ffffff; border-radius: 8px; padding: 20px; margin: 15px 0; border-left: 4px solid #4fc3f7;\">\n" +
        "                                                        <p style=\"color: #1a4f8b; font-size: 18px; margin: 0; font-weight: 600;\">💎 Incluye:</p>\n" +
        "                                                        <ul style=\"color: #444444; text-align: left; margin: 10px 0; padding-left: 20px;\">\n" +
        "                                                            <li style=\"margin: 8px 0;\">✅ Consultoría personalizada</li>\n" +
        "                                                            <li style=\"margin: 8px 0;\">✅ Soporte 24/7</li>\n" +
        "                                                            <li style=\"margin: 8px 0;\">✅ Herramientas premium</li>\n" +
        "                                                            <li style=\"margin: 8px 0;\">✅ Garantía extendida</li>\n" +
        "                                                        </ul>\n" +
        "                                                    </div>\n" +
        "                                                </td>\n" +
        "                                            </tr>\n" +
        "                                        </table>\n" +
        "                                        \n" +
        "                                        <!-- Botón de Acción -->\n" +
        "                                        <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"margin: 30px auto;\">\n" +
        "                                            <tr>\n" +
        "                                                <td style=\"background: linear-gradient(135deg, #3d8ad8 0%, #2d6cb0 100%); border-radius: 30px; padding: 15px 40px; text-align: center; box-shadow: 0 4px 15px rgba(61, 138, 216, 0.4);\">\n" +
        "                                                    <a href=\"https://xtartsolutions-crm.onrender.com/ventas\" style=\"color:rgb(143, 145, 255); text-decoration: none; font-weight: 600; font-size: 18px; display: block;\">🎯 ¡Aprovechar Oferta Ahora!</a>\n" +
        "                                                </td>\n" +
        "                                            </tr>\n" +
        "                                        </table>\n" +
        "                                        \n" +
        "                                        <p style=\"color: #ff6b35; font-size: 16px; font-weight: 600; margin: 20px 0;\">⏰ ¡Oferta válida solo hasta el 30 de Junio!</p>\n" +
        "                                        \n" +
        "                                        <hr style=\"border: none; height: 1px; background: linear-gradient(90deg, transparent, #3d8ad8, transparent); margin: 30px 0;\">\n" +
        "                                        \n" +
        "                                        <p style=\"color: #666666; font-size: 15px; line-height: 1.6; margin: 20px 0;\">¿Tienes preguntas? Nuestro equipo está aquí para ayudarte.<br>Responde a este correo o llámanos al <strong style=\"color: #1a4f8b;\">📞 (555) 123-4567</strong></p>\n" +
        "                                    </td>\n" +
        "                                </tr>\n" +
        "                            </table>\n" +
        "                        </td>\n" +
        "                    </tr>\n" +
        "                    \n" +
        "                    <!-- Footer -->\n" +
        "                    <tr>\n" +
        "                        <td style=\"background-color: #0a2e55; padding: 25px; text-align: center;\">\n" +
        "                            <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
        "                                <tr>\n" +
        "                                    <td style=\"text-align: center;\">\n" +
        "                                        <p style=\"color: #a0b4cc; margin: 0 0 10px; font-size: 14px;\">Gracias por confiar en nosotros</p>\n" +
        "                                        <h3 style=\"color: #4fc3f7; margin: 0 0 15px; font-size: 18px; font-weight: 600;\">Xtart Solutions</h3>\n" +
        "                                        \n" +
        "                                        <!-- Redes Sociales -->\n" +
        "                                        <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" style=\"margin: 15px auto;\">\n" +
        "                                            <tr>\n" +
        "                                                <td style=\"padding: 0 8px;\">\n" +
        "                                                    <a href=\"#\" style=\"color: #4fc3f7; text-decoration: none; font-size: 18px;\">📧</a>\n" +
        "                                                </td>\n" +
        "                                                <td style=\"padding: 0 8px;\">\n" +
        "                                                    <a href=\"#\" style=\"color: #4fc3f7; text-decoration: none; font-size: 18px;\">🌐</a>\n" +
        "                                                </td>\n" +
        "                                                <td style=\"padding: 0 8px;\">\n" +
        "                                                    <a href=\"#\" style=\"color: #4fc3f7; text-decoration: none; font-size: 18px;\">📱</a>\n" +
        "                                                </td>\n" +
        "                                            </tr>\n" +
        "                                        </table>\n" +
        "                                        \n" +
        "                                        <p style=\"color: #666666; font-size: 12px; margin: 15px 0 0; line-height: 1.4;\">Si no deseas recibir más correos como este, <a href=\"#\" style=\"color: #4fc3f7;\">haz clic aquí</a><br>© 2024 Xtart Solutions. Todos los derechos reservados.</p>\n" +
        "                                    </td>\n" +
        "                                </tr>\n" +
        "                            </table>\n" +
        "                        </td>\n" +
        "                    </tr>\n" +
        "                </table>\n" +
        "            </td>\n" +
        "        </tr>\n" +
        "    </table>\n" +
        "</body>\n" +
        "</html>";
}

private String crearHtmlRespuesta(String mensaje, String tipoMensaje, String iconoMensaje) {
    return "<!DOCTYPE html>\n" +
        "<html lang=\"es\">\n" +
        "<head>\n" +
        "    <meta charset=\"UTF-8\">\n" +
        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
        "    <title>Envío de Oferta - Xtart Solutions</title>\n" +
        "    <style>\n" +
        "        :root {\n" +
        "            --primary-color: #1a4f8b;\n" +
        "            --secondary-color: #2d6cb0;\n" +
        "            --accent-color: #3d8ad8;\n" +
        "            --accent-glow: #4fc3f7;\n" +
        "            --dark-blue: #0a2e55;\n" +
        "            --darker-blue: #061c34;\n" +
        "            --text-color: #e0e6ef;\n" +
        "            --text-light: #a0b4cc;\n" +
        "            --white: #ffffff;\n" +
        "        }\n" +
        "\n" +
        "        .welcome-page {\n" +
        "            font-family: 'Segoe UI', 'Helvetica Neue', Arial, sans-serif;\n" +
        "            background-color: #091828;\n" +
        "            color: var(--text-color);\n" +
        "            line-height: 1.6;\n" +
        "            min-height: 100vh;\n" +
        "            display: flex;\n" +
        "            flex-direction: column;\n" +
        "            margin: 0;\n" +
        "            padding: 0;\n" +
        "        }\n" +
        "\n" +
        "        .header {\n" +
        "            background: linear-gradient(135deg, var(--primary-color), var(--dark-blue));\n" +
        "            padding: 20px 0;\n" +
        "            text-align: center;\n" +
        "        }\n" +
        "\n" +
        "        .header-title {\n" +
        "            color: var(--white);\n" +
        "            margin: 0;\n" +
        "            font-size: 28px;\n" +
        "        }\n" +
        "\n" +
        "        .main-content {\n" +
        "            flex: 1;\n" +
        "            padding: 40px 20px;\n" +
        "            display: flex;\n" +
        "            justify-content: center;\n" +
        "            align-items: center;\n" +
        "        }\n" +
        "\n" +
        "        .welcome-container {\n" +
        "            background-color: rgba(10, 46, 85, 0.6);\n" +
        "            border-radius: 12px;\n" +
        "            padding: 40px;\n" +
        "            max-width: 600px;\n" +
        "            text-align: center;\n" +
        "            border: 1px solid rgba(79, 195, 247, 0.2);\n" +
        "        }\n" +
        "\n" +
        "        .status-icon {\n" +
        "            font-size: 48px;\n" +
        "            margin-bottom: 20px;\n" +
        "            display: block;\n" +
        "        }\n" +
        "\n" +
        "        .status-title {\n" +
        "            color: var(--white);\n" +
        "            font-size: 28px;\n" +
        "            margin-bottom: 20px;\n" +
        "        }\n" +
        "\n" +
        "        .status-message {\n" +
        "            font-size: 18px;\n" +
        "            margin-bottom: 30px;\n" +
        "            padding: 20px;\n" +
        "            border-radius: 8px;\n" +
        "            border: 1px solid;\n" +
        "        }\n" +
        "\n" +
        "        .success {\n" +
        "            background-color: rgba(76, 175, 80, 0.1);\n" +
        "            color: #81c784;\n" +
        "            border-color: rgba(76, 175, 80, 0.3);\n" +
        "        }\n" +
        "\n" +
        "        .error {\n" +
        "            background-color: rgba(244, 67, 54, 0.1);\n" +
        "            color: #e57373;\n" +
        "            border-color: rgba(244, 67, 54, 0.3);\n" +
        "        }\n" +
        "\n" +
        "        .warning {\n" +
        "            background-color: rgba(255, 152, 0, 0.1);\n" +
        "            color: #ffb74d;\n" +
        "            border-color: rgba(255, 152, 0, 0.3);\n" +
        "        }\n" +
        "\n" +
        "        .dashboard-btn {\n" +
        "            background: linear-gradient(135deg, var(--accent-color), var(--secondary-color));\n" +
        "            color: var(--white);\n" +
        "            text-decoration: none;\n" +
        "            padding: 14px 30px;\n" +
        "            border-radius: 30px;\n" +
        "            font-size: 16px;\n" +
        "            display: inline-block;\n" +
        "            margin: 0 10px;\n" +
        "            transition: all 0.3s ease;\n" +
        "        }\n" +
        "\n" +
        "        .dashboard-btn:hover {\n" +
        "            transform: translateY(-3px);\n" +
        "        }\n" +
        "    </style>\n" +
        "</head>\n" +
        "<body class=\"welcome-page\">\n" +
        "    <header class=\"header\">\n" +
        "        <h1 class=\"header-title\">Xtart Solutions</h1>\n" +
        "    </header>\n" +
        "\n" +
        "    <main class=\"main-content\">\n" +
        "        <div class=\"welcome-container\">\n" +
        "            <span class=\"status-icon\">" + iconoMensaje + "</span>\n" +
        "            <h2 class=\"status-title\">Envío de Oferta</h2>\n" +
        "            <div class=\"status-message " + tipoMensaje + "\">\n" +
        "                " + mensaje + "\n" +
        "            </div>\n" +
        "            <div>\n" +
        "                <a href=\"/clientes\" class=\"dashboard-btn\">← Volver a Clientes</a>\n" +
        "                <a href=\"/dashboard\" class=\"dashboard-btn\">🏠 Dashboard</a>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </main>\n" +
        "</body>\n" +
        "</html>";
}

    // 8. Ver clientes más frecuentes
    @GetMapping("/frecuentes")
    public String clientesFrecuentes() {
        List<Object[]> resultados = facturaRepository.findClientesFrecuentes();
        StringBuilder sb = new StringBuilder("Clientes más frecuentes:\n");
        
        if (resultados.isEmpty()) {
            return "No hay información de clientes frecuentes disponible.";
        }
        
        for (int i = 0; i < Math.min(resultados.size(), 5); i++) {
            Cliente cliente = (Cliente) resultados.get(i)[0];
            Long totalCompras = (Long) resultados.get(i)[1];
            sb.append((i + 1)).append(". ").append(cliente.getNombre())
              .append(" - ").append(totalCompras).append(" compras\n");
        }
        return sb.toString();
    }

    // 9. Ver clientes inactivos
    @GetMapping("/inactivos")
    public List<Cliente> clientesInactivos() {
        return clienteRepository.findByActivoFalse();
    }

    // 12. Activar/Desactivar cliente
    @PatchMapping("/{id}/estado")
    public Cliente cambiarEstadoCliente(@PathVariable Long id, @RequestParam Boolean activo) {
        Optional<Cliente> optCliente = clienteRepository.findById(id);
        if (optCliente.isPresent()) {
            Cliente cliente = optCliente.get();
            cliente.setActivo(activo);
            return clienteRepository.save(cliente);
        }
        return null;
    }
    
    // Clase auxiliar para el conteo
    public static class ConteoClientes {
        public final long total;
        public final long activos;
        public final long inactivos;
        
        public ConteoClientes(long total, long activos, long inactivos) {
            this.total = total;
            this.activos = activos;
            this.inactivos = inactivos;
        }
    }
    
    // 13. Contar clientes por estado
    @GetMapping("/conteo")
    public ConteoClientes contarClientesPorEstado() {
        long total = clienteRepository.count();
        long activos = clienteRepository.countByActivoTrue();
        long inactivos = clienteRepository.countByActivoFalse();
        
        return new ConteoClientes(total, activos, inactivos);
    }
}