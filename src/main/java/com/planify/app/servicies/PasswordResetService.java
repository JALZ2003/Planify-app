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
        if (email != null){
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

                String message = "<h1>Código de recuperación</h1><p>Tu código es: <b>" + code.getCode() + "</b></p>";
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
            if (resetCode.isPresent()){

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
