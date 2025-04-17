package com.planify.app.servicies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationDispatcher {

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    public void sendNotification(String to, String subject, String message, String type) {
        if ("email".equalsIgnoreCase(type)) {
            emailService.send(to, subject, message);
        } else if ("sms".equalsIgnoreCase(type)) {
            smsService.send(to, subject, message);
        }
    }

}
