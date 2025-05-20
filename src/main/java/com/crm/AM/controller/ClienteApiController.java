package com.crm.AM.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
    public String enviarOferta(@PathVariable Long id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            String email = cliente.getEmail();
            if (email == null || email.trim().isEmpty()) {
                return "El cliente no tiene email registrado";
            }
            
            String asunto = "¡Oferta exclusiva para ti!";
            String contenido = "Hola " + cliente.getNombre() + ",\n\nTenemos una oferta especial solo para ti. ¡No te la pierdas!";

            emailService.enviarCorreoOferta(email, asunto, contenido);
            return "Correo enviado a " + cliente.getNombre();
        } else {
            return "Cliente no encontrado.";
        }
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