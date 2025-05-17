package com.planify.app.servicies;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String from;

    @Override
    public void send(String to, String subject, String htmlMessage) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper =  new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlMessage, true);

            mailSender.send(message);
        }catch (MessagingException e){
            throw new RuntimeException("Error to send email!!", e);
        }
    }
}