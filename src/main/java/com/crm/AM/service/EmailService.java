package com.crm.AM.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    //Necesario javaMailSender
    //Hay que configurar el application properties con el email desde donde se va a enviar el correo con un token
    //El metodo es bastante sencillo de enteder no requiere explicacion 

    public void enviarCorreoOferta(String destinatario, String asunto, String contenido) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(contenido);
        mensaje.setFrom("tu_correo@gmail.com");

        mailSender.send(mensaje);
    }
}

