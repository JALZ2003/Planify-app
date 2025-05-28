package com.planify.app.validations;

import com.planify.app.dtos.DtoResponse;
import com.planify.app.dtos.DtoScheduledSaving;
import com.planify.app.dtos.DtoTransaction;
import com.planify.app.models.ScheduledSaving;
import com.planify.app.models.User;
import com.planify.app.repositories.ScheduledSavingRepository;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Component
public class ScheduledSavingValidation {

    private final UserRepository userRepository;
    private final JwtGenerador jwtGenerador;
    private  final ScheduledSavingRepository scheduledSavingRepository;

    public ScheduledSavingValidation(UserRepository userRepository,
                                     ScheduledSavingRepository scheduledSavingRepository,
                                     JwtGenerador jwtGenerador){

        this.userRepository = userRepository;
        this.scheduledSavingRepository = scheduledSavingRepository;
        this.jwtGenerador = jwtGenerador;
    }

    private ResponseEntity<?> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(DtoResponse.builder()
                        .success(false)
                        .message(message)
                        .build());
    }



    public ResponseEntity<?> validationSaving(String token, DtoScheduledSaving dtoScheduledSaving){
        // Validación básica del token
        if (token == null || token.isBlank()) {
            return buildErrorResponse("Token no proporcionado", HttpStatus.BAD_REQUEST);
        }

        // Extraer token Bearer
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Validar usuario
        String idUserFromToken;
        try {
            idUserFromToken = jwtGenerador.extractId(token);
        } catch (Exception e) {
            return buildErrorResponse("Token inválido", HttpStatus.UNAUTHORIZED);
        }

        Optional<User> optionalUser = userRepository.findById(Long.parseLong(idUserFromToken));
        if (optionalUser.isEmpty()) {
            return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
        }
        User user = optionalUser.get();

        // Validación de datos de transacción
        if (dtoScheduledSaving == null) {
            return buildErrorResponse("Datos del Notebook son requeridos", HttpStatus.BAD_REQUEST);
        }

        if (dtoScheduledSaving.getAmount() == null || dtoScheduledSaving.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return buildErrorResponse("Monto inválido", HttpStatus.BAD_REQUEST);
        }

        // Si todo está bien, retornar null (sin errores)
        return null;
    }
}
