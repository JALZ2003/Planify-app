package com.planify.app.servicies;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService implements NotificationService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhone;

    private boolean initialized = false;

    private void init() {
        if (!initialized) {
            Twilio.init(accountSid, authToken);
            initialized = true;
        }
    }

    @Override
    public void send(String to, String subject, String message) {
        init(); // Inicializar Twilio una sola vez

        Message.creator(
                new com.twilio.type.PhoneNumber("+57"+to),
                new com.twilio.type.PhoneNumber(fromPhone),
                message
        ).create();
    }
}
