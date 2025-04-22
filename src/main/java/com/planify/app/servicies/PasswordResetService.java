package com.planify.app.servicies;

import com.planify.app.dtos.DtoResponse;
import com.planify.app.models.PasswordResetCode;
import com.planify.app.models.User;
import com.planify.app.repositories.PasswordResetCodeRepository;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetCodeRepository resetCodeRepository;

    @Autowired
    private NotificationDispatcher dispatcher;

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public ResponseEntity<?> sendResetCode(String email, String phone) {
        if (email != null) {
            Optional<User> user = userRepository.findByEmail(email);

            if (user.isPresent()) {
                PasswordResetCode code = resetCodeRepository.save(
                        PasswordResetCode.builder()
                                .user(user.get())
                                .code(generateCode())
                                .expiration(LocalDateTime.now().plusMinutes(10))
                                .used(false)
                                .build()
                );

                String message = String.format("""
         <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #f9f9f9;">
            <h2 style="color: #2c3e50; text-align: center;">🔐 Recuperación de Cuenta - PlaniFy</h2>
            <p style="font-size: 16px; color: #333;">Hola, %s:</p>
            <p style="font-size: 16px; color: #333;">
                Has solicitado recuperar el acceso a tu cuenta. Por favor, utiliza el siguiente código para continuar con el proceso de recuperación:
             </p>
        <div style="text-align: center; margin: 20px 0;">
            <span style="font-size: 24px; font-weight: bold; color: #2980b9;">%s</span>
        </div>
            <p style="font-size: 14px; color: #555;">
                Si no solicitaste esta acción, puedes ignorar este correo de forma segura.
            </p>
            <p style="font-size: 14px; color: #555;">
            Gracias por confiar en <strong>PlaniFy</strong>.
            </p>
            <hr style="margin-top: 30px;">
            <p style="font-size: 12px; color: #999; text-align: center;">
                © PlaniFy 2025. Todos los derechos reservados.
            </p>
        </div>
               """, user.get().getName(), code.getCode());
                dispatcher.sendNotification(email, "Codigo de recuperación", message, "email");
                return ResponseEntity.ok(DtoResponse.builder()
                        .success(true)
                        .response("Email send to: " + email)
                        .message("Email sended succesful!!")
                        .build());
            }
        } else if (phone != null) {
            Optional<User> user = userRepository.findByPhoneNumber(phone);

            if (user.isPresent()) {
                PasswordResetCode code = resetCodeRepository.save(
                        PasswordResetCode.builder()
                                .user(user.get())
                                .code(generateCode())
                                .expiration(LocalDateTime.now().plusMinutes(10))
                                .used(false)
                                .build()
                );

                String message = "<h1>Código de recuperación</h1><p>Tu código es: <b>" + code.getCode() + "</b></p>";
                dispatcher.sendNotification(phone, "Codigo de recuperación", message, "sms");
                return ResponseEntity.ok(DtoResponse.builder()
                        .success(true)
                        .response("Phone send to: " + phone)
                        .message("Phone sended succesful!!")
                        .build());
            }
        }
        return ResponseEntity.status(404).body(
                DtoResponse.builder()
                        .success(false)
                        .response(null)
                        .message("User not found!!")
                        .build()
        );
    }

    public ResponseEntity<?> verifyCode(String email, String code) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            Optional<PasswordResetCode> codeFound = resetCodeRepository.findByUserAndCodeAndUsedIsFalse(user.get(), code);
            if (codeFound.isPresent()) {
                PasswordResetCode resetCode = codeFound.get();
                if (resetCode.getExpiration().isAfter(LocalDateTime.now())) {
                    resetCode.setUsed(true);
                    resetCodeRepository.save(resetCode);
                    return ResponseEntity.ok(DtoResponse.builder()
                            .success(true)
                            .response(codeFound.get().getCode())
                            .message("The code is valid!!")
                            .build());
                }
            }
        }
        return ResponseEntity.status(404).body(
                DtoResponse.builder()
                        .success(false)
                        .response(null)
                        .message("The code is invalid or has expired!!")
                        .build()
        );
    }

    public ResponseEntity<?> resetPassword(String email, String code, String newPassword) {

        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {

            Optional<PasswordResetCode> resetCode = resetCodeRepository.findByUserAndCode(user.get(), code);
            if (resetCode.isPresent()) {

                System.out.println(resetCode.get().getExpiration().isBefore(LocalDateTime.now()));
                System.out.println(resetCode.get().getExpiration());
                System.out.println(LocalDateTime.now());

                if (resetCode.get().getExpiration().isBefore(LocalDateTime.now())) {
                    return ResponseEntity.status(404).body(
                            DtoResponse.builder()
                                    .success(false)
                                    .response(null)
                                    .message("The code is invalid or has expired!!")
                                    .build()
                    );
                }
                passwordEncoder = new BCryptPasswordEncoder();
                user.get().setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user.get());
                return ResponseEntity.ok(
                        DtoResponse.builder()
                                .success(true)
                                .response("The recover password is success!!")
                                .message("Tha recover password is success!!")
                                .build()
                );
            }
        }
        return ResponseEntity.status(404).body(
                DtoResponse.builder()
                        .success(false)
                        .response(null)
                        .message("The code is invalid or has expired!!")
                        .build()
        );
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}
