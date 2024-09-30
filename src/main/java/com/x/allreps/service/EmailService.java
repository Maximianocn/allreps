package com.x.allreps.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // Injeção de dependência via construtor
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendResetCodeEmail(String toEmail, String resetCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Código de Redefinição de Senha");
        message.setText("Seu código de redefinição de senha é: " + resetCode);
        mailSender.send(message);
    }

    public void sendEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ALLREPS PRECISA DE VOCE!!");
        message.setText("O LINK PARA O DISCORD: https://discord.gg/4R4249tr: " +
                "MAXIMIANO PRECISA DE VOCE AGORA, LIGUE AGORA PARA +55 35 99820-2357");
        mailSender.send(message);
    }

}
