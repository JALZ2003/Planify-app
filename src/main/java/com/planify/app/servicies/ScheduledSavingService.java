package com.planify.app.servicies;

import com.planify.app.dtos.DtoResponse;
import com.planify.app.dtos.DtoScheduledSaving;
import com.planify.app.models.ScheduledSaving;
import com.planify.app.models.User;
import com.planify.app.repositories.ScheduledSavingRepository;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import com.planify.app.validations.ScheduledSavingValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduledSavingService {


    @Autowired
    private ScheduledSavingRepository scheduledSavingRepository;

    @Autowired
    private JwtGenerador jwtGenerador;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduledSavingValidation scheduledSavingValidation;


    private ResponseEntity<DtoResponse> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(DtoResponse.builder().success(false).message(message).response(null).build());
    }

    public ResponseEntity<?> createNotebook(String token, DtoScheduledSaving dtoScheduledSaving) {

        try {
            // Validar la transacción
            ResponseEntity<?> validationResponse = scheduledSavingValidation.validationSaving(token, dtoScheduledSaving);
            if (validationResponse != null) {
                return validationResponse;
            }

            // Extraer usuario (ya validado)
            String idUserFromToken = jwtGenerador.extractId(token.substring(7));
            User user = userRepository.findById(Long.parseLong(idUserFromToken)).get();

            ScheduledSaving scheduledSaving = ScheduledSaving.builder()
                    .user(user)
                    .name(dtoScheduledSaving.getName())
                    .amount(dtoScheduledSaving.getAmount())
                    .startDate(LocalDate.now())
                    .goalAmount(dtoScheduledSaving.getGoalAmount())
                    .build();

            ScheduledSaving savedScheduledSaving = scheduledSavingRepository.save(scheduledSaving);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DtoResponse.builder().success(true).
                            message("Notbook creada exitosamente").response(savedScheduledSaving.getId()).build());

        } catch (NumberFormatException e) {
            return buildErrorResponse("Formato de ID de usuario inválido", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al crear Notbook: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateNotbook(String token, Long idScheduledSaving, DtoScheduledSaving dto) {
        try {
            // Validar la transacción
            ResponseEntity<?> validationResponse = scheduledSavingValidation.validationSaving(token, dto);
            if (validationResponse != null) {
                return validationResponse;
            }

            // Extraer usuario (ya validado)
            String idUserFromToken = jwtGenerador.extractId(token.substring(7));
            User user = userRepository.findById(Long.parseLong(idUserFromToken)).get();

            Optional<ScheduledSaving> optionalNotbook = scheduledSavingRepository.findById(idScheduledSaving);
            if (optionalNotbook.isEmpty()) {
                return buildErrorResponse("Notbook no encontrado", HttpStatus.NOT_FOUND);
            }

            ScheduledSaving notbook = optionalNotbook.get();
            // Verificar que el notbook pertenece al usuario
            if (notbook.getUser() == null || !notbook.getUser().getId().equals(user.getId())) {
                return buildErrorResponse("No tienes permiso para modificar este Notbook", HttpStatus.FORBIDDEN);
            }

            // Actualizar nombre si viene
            if (dto.getName() != null) {
                notbook.setName(dto.getName());
            }
            // Actualizar la meta (goalAmount) si se envía en el DTO
            if (dto.getGoalAmount() != null) {
                notbook.setGoalAmount(dto.getGoalAmount());
            }

            // Sumar nuevo amount al existente
            if (dto.getAmount() != null) {
                BigDecimal newTotal = notbook.getAmount().add(dto.getAmount());
                // Validación opcional: no exceder la meta
                if (notbook.getGoalAmount() != null && newTotal.compareTo(notbook.getGoalAmount()) > 0) {
                    return buildErrorResponse("No puedes ahorrar más de la meta establecida", HttpStatus.BAD_REQUEST);
                }
                notbook.setAmount(newTotal);
            }

            ScheduledSaving updateNotbook = scheduledSavingRepository.save(notbook);

            // Construir DTO de respuesta
            DtoScheduledSaving responseDto = DtoScheduledSaving.builder()
                    .id(updateNotbook.getId())
                    .name(updateNotbook.getName())
                    .amount(updateNotbook.getAmount())
                    .startDate(updateNotbook.getStartDate())
                    .goalAmount(updateNotbook.getGoalAmount())
                    .build();

            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .message("Notbook actualizado exitosamente")
                    .response(responseDto).build());

        } catch (Exception e) {
            return buildErrorResponse("Error al actualizar notbook: " +
                    e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getAllNotbooks(String token) {
        try {
            String idUserFromToken = jwtGenerador.extractId(token.substring(7));
            User user = userRepository.findById(Long.parseLong(idUserFromToken)).orElse(null);
            if (user == null) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.UNAUTHORIZED);
            }

            List<ScheduledSaving> savings = scheduledSavingRepository.findByUser(user);

            List<DtoScheduledSaving> responseList = savings.stream().map(saving ->
                    DtoScheduledSaving.builder()
                            .id(saving.getId())
                            .name(saving.getName())
                            .amount(saving.getAmount())
                            .startDate(saving.getStartDate())
                            .goalAmount(saving.getGoalAmount())
                            .build()
            ).toList();

            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .message("Lista de ahorros programados")
                    .response(responseList)
                    .build());

        } catch (Exception e) {
            return buildErrorResponse("Error al obtener ahorros: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getNotbookById(String token, Long idScheduledSaving) {
        try {
            String idUserFromToken = jwtGenerador.extractId(token.substring(7));
            User user = userRepository.findById(Long.parseLong(idUserFromToken)).orElse(null);

            Optional<ScheduledSaving> optional = scheduledSavingRepository.findById(idScheduledSaving);
            if (optional.isEmpty()) {
                return buildErrorResponse("Notbook no encontrado", HttpStatus.NOT_FOUND);
            }

            ScheduledSaving saving = optional.get();

            if (!saving.getUser().getId().equals(user.getId())) {
                return buildErrorResponse("No tienes permiso para ver este ahorro", HttpStatus.FORBIDDEN);
            }

            DtoScheduledSaving response = DtoScheduledSaving.builder()
                    .id(saving.getId())
                    .name(saving.getName())
                    .amount(saving.getAmount())
                    .startDate(saving.getStartDate())
                    .goalAmount(saving.getGoalAmount())
                    .build();

            return ResponseEntity.ok(DtoResponse.builder().success(true).response(response).build());

        } catch (Exception e) {
            return buildErrorResponse("Error al obtener notbook: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteNotbook(String token, Long idScheduledSaving) {
        try {
            String idUserFromToken = jwtGenerador.extractId(token.substring(7));
            User user = userRepository.findById(Long.parseLong(idUserFromToken)).orElse(null);

            Optional<ScheduledSaving> optional = scheduledSavingRepository.findById(idScheduledSaving);
            if (optional.isEmpty()) {
                return buildErrorResponse("Notbook no encontrado", HttpStatus.NOT_FOUND);
            }

            ScheduledSaving saving = optional.get();

            if (!saving.getUser().getId().equals(user.getId())) {
                return buildErrorResponse("No tienes permiso para eliminar este ahorro", HttpStatus.FORBIDDEN);
            }

            scheduledSavingRepository.delete(saving);
            return ResponseEntity.ok(DtoResponse.builder().success(true).message("Ahorro eliminado correctamente").build());

        } catch (Exception e) {
            return buildErrorResponse("Error al eliminar notbook: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}


