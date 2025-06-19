package com.planify.app.servicies;

import com.planify.app.dtos.DtoResponse;
import com.planify.app.dtos.DtoTransaction;
import com.planify.app.dtos.DtoTransactionCategory;
import com.planify.app.models.Category;
import com.planify.app.models.Transaction;
import com.planify.app.models.User;
import com.planify.app.repositories.CategoryRepository;
import com.planify.app.repositories.FlowTypeRepository;
import com.planify.app.repositories.TransactionRepository;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import com.planify.app.validations.TransactionValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionValidation transactionValidator;
    private final TransactionRepository transactionRepository;
    @Autowired
    private JwtGenerador jwtGenerador;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    public TransactionService(TransactionValidation transactionValidator, TransactionRepository transactionRepository) {
        this.transactionValidator = transactionValidator;
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<?> createTransaction(String token, DtoTransaction dtoTransaction) {
        try {
            // Validar la transacción
            ResponseEntity<?> validationResponse = transactionValidator.validateTransactionCreation(token, dtoTransaction);
            if (validationResponse != null) {
                return validationResponse;
            }

            // Extraer usuario (ya validado)
            String idUserFromToken = jwtGenerador.extractId(token.substring(7));
            User user = userRepository.findById(Long.parseLong(idUserFromToken)).get();

            // Extraer categoría (ya validada)
            Category category = categoryRepository.findById(dtoTransaction.getCategoryId()).get();

            // Crear transacción
            Transaction transaction = Transaction.builder()
                    .user(user)
                    .category(category)
                    .amount(dtoTransaction.getAmount())
                    .description(dtoTransaction.getDescription())
                    .date(LocalDate.now()).build();

            Transaction savedTransaction = transactionRepository.save(transaction);

            return ResponseEntity.status(HttpStatus.CREATED).body(DtoResponse.builder().success(true).message("Transacción creada exitosamente").response(savedTransaction.getId()).build());

        } catch (NumberFormatException e) {
            return buildErrorResponse("Formato de ID de usuario inválido", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al crear transacción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<?> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(DtoResponse.builder().success(false).message(message).build());
    }

    public ResponseEntity<?> deleteTransaction(String token, Long transactionId) {
        try {
            // Validar la operación de eliminación
            ResponseEntity<?> validationResponse = transactionValidator.validateTransactionDeletion(token, transactionId);
            if (validationResponse != null) {
                return validationResponse;
            }

            // Eliminar la transacción (ya validada)
            transactionRepository.deleteById(transactionId);

            return ResponseEntity.ok(DtoResponse.builder().success(true).message("Transacción eliminada exitosamente").build());

        } catch (Exception e) {
            return buildErrorResponse("Error al eliminar transacción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> updateTransaction(String token, Long transactionId, DtoTransaction dtoTransaction) {
        try {
            if (token == null || token.isBlank()) {
                return buildErrorResponse("Token no proporcionado", HttpStatus.BAD_REQUEST);
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String userId = jwtGenerador.extractId(token);
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(userId));
            if (optionalUser.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            User user = optionalUser.get();

            Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
            if (optionalTransaction.isEmpty()) {
                return buildErrorResponse("Transacción no encontrada", HttpStatus.NOT_FOUND);
            }
            Transaction existingTransaction = optionalTransaction.get();

            if (!existingTransaction.getUser().getId().equals(user.getId())) {
                return buildErrorResponse("No autorizado para modificar esta transacción", HttpStatus.FORBIDDEN);
            }

            // Validar nueva categoría
            Optional<Category> optionalCategory = categoryRepository.findById(dtoTransaction.getCategoryId());
            if (optionalCategory.isEmpty()) {
                return buildErrorResponse("Categoría no encontrada", HttpStatus.NOT_FOUND);
            }
            Category category = optionalCategory.get();

            // Validación lógica si es necesario
            ResponseEntity<?> validation = transactionValidator.validateTransactionUpdate(dtoTransaction, user, category, transactionId);
            if (validation != null) return validation;

            // Actualizar los campos permitidos
            existingTransaction.setAmount(dtoTransaction.getAmount());
            existingTransaction.setDescription(dtoTransaction.getDescription());
            existingTransaction.setCategory(category);
            // No actualices el createdAt

            transactionRepository.save(existingTransaction);

            return ResponseEntity.ok(DtoResponse.builder().success(true).message("Transacción actualizada exitosamente").response(existingTransaction.getId()).build());

        } catch (Exception e) {
            return buildErrorResponse("Error al actualizar transacción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getAllTransactionsForUser(String token, LocalDate date) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Long userId = Long.parseLong(jwtGenerador.extractId(token));

            // Verificación más eficiente que cargar todo el usuario
            if (!userRepository.existsById(userId)) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }

            List<Transaction> transactions = transactionRepository.findByUserIdAndDate(userId, date != null ? date : LocalDate.now());

            // Mapeo a DTOs usando el método estático
            List<DtoTransactionCategory> transactionDtos = transactions.stream().map(DtoTransactionCategory::from).collect(Collectors.toList());

            return ResponseEntity.ok(DtoResponse.builder().success(true).message("Transacciones del usuario").response(transactionDtos).build());
        } catch (Exception e) {
            return buildErrorResponse("Error al obtener transacciones: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getTransactionById(String token, Long transactionId) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Long userId = Long.parseLong(jwtGenerador.extractId(token));

            // Verificación más eficiente (solo necesita saber si existe)
            if (!userRepository.existsById(userId)) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }

            Optional<Transaction> optionalTransaction = transactionRepository.findByIdAndUserId(transactionId, userId);

            if (optionalTransaction.isEmpty()) {
                return buildErrorResponse("Transacción no encontrada o no pertenece al usuario", HttpStatus.NOT_FOUND);
            }

            // Convertir Transaction a DtoTransaction
            Transaction transaction = optionalTransaction.get();
            DtoTransaction transactionDto = DtoTransaction.from(transaction);

            return ResponseEntity.ok(DtoResponse.builder().success(true).message("Transacción encontrada").response(transactionDto)  // Usar el DTO en lugar de la entidad
                    .build());

        } catch (Exception e) {
            return buildErrorResponse("Error al obtener transacción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getUserFinancialSummary(String token, LocalDate date) {
        try {
            if (token == null || token.isBlank()) {
                return buildErrorResponse("Token no proporcionado", HttpStatus.BAD_REQUEST);
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Long userId = Long.parseLong(jwtGenerador.extractId(token));

            if (!userRepository.existsById(userId)) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }

            List<Transaction> transactions = transactionRepository.findByUserIdAndDate(userId, date != null ? date : LocalDate.now());

            BigDecimal totalIngresos = BigDecimal.ZERO;
            BigDecimal totalGastos = BigDecimal.ZERO;

            for (Transaction t : transactions) {
                String tipoFlujo = t.getCategory().getFlowType().getName().trim().toUpperCase();

                if (tipoFlujo.equals("INGRESOS")) {
                    totalIngresos = totalIngresos.add(t.getAmount());
                } else if (tipoFlujo.equals("GASTOS")) {
                    totalGastos = totalGastos.add(t.getAmount());
                }
            }

            BigDecimal saldoFinal = totalIngresos.subtract(totalGastos);

            Map<String, Object> resumen = new HashMap<>();
            resumen.put("totalIngresos", totalIngresos);
            resumen.put("totalGastos", totalGastos);
            resumen.put("saldoFinal", saldoFinal);

            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .message("Resumen financiero del usuario")
                    .response(resumen)
                    .build());

        } catch (Exception e) {
            return buildErrorResponse("Error al obtener resumen financiero: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}