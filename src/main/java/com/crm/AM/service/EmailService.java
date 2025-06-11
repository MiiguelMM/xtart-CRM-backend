package com.crm.AM.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void enviarCorreoOferta(String destinatario, String asunto, String contenidoHtml) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenidoHtml, true); // true = es HTML
            helper.setFrom("tu-email@empresa.com");
            
            mailSender.send(mensaje);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }
}

//Necesario javaMailSender
    //Hay que configurar el application properties con el email desde donde se va a enviar el correo con un token
    //El metodo es bastante sencillo de enteder no requiere explicacion 