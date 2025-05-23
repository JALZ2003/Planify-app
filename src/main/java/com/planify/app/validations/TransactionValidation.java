package com.planify.app.validations;

import com.planify.app.dtos.DtoResponse;
import com.planify.app.dtos.DtoTransaction;
import com.planify.app.models.Category;
import com.planify.app.models.Transaction;
import com.planify.app.models.User;
import com.planify.app.repositories.CategoryRepository;
import com.planify.app.repositories.TransactionRepository;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TransactionValidation {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final JwtGenerador jwtGenerador;

    public TransactionValidation(UserRepository userRepository,
                                CategoryRepository categoryRepository,
                                TransactionRepository transactionRepository,
                                JwtGenerador jwtGenerador) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.jwtGenerador = jwtGenerador;
    }

    public ResponseEntity<?> validateTransactionCreation(String token, DtoTransaction dtoTransaction) {
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
        if (dtoTransaction == null) {
            return buildErrorResponse("Datos de la transacción son requeridos", HttpStatus.BAD_REQUEST);
        }

        if (dtoTransaction.getAmount() == null || dtoTransaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return buildErrorResponse("Monto inválido", HttpStatus.BAD_REQUEST);
        }

        if (dtoTransaction.getCategoryId() == null) {
            return buildErrorResponse("La categoría es requerida", HttpStatus.BAD_REQUEST);
        }

        // Validación de categoría
        Optional<Category> optionalCategory = categoryRepository.findById(dtoTransaction.getCategoryId());
        if (optionalCategory.isEmpty()) {
            return buildErrorResponse("Categoría no encontrada", HttpStatus.NOT_FOUND);
        }
        Category category = optionalCategory.get();

        // Validar permisos sobre categoría
        if (category.isFixed()) {
            if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
                return buildErrorResponse("No autorizado para usar esta categoría", HttpStatus.FORBIDDEN);
            }
        }

        // Validar transacción duplicada
        if (isDuplicateTransaction(user, dtoTransaction)) {
            return buildErrorResponse("Transacción duplicada", HttpStatus.CONFLICT);
        }

        // Si todo está bien, retornar null (sin errores)
        return null;
    }

    private boolean isDuplicateTransaction(User user, DtoTransaction dtoTransaction) {
        // Aquí defines qué consideras una transacción duplicada
        // Por ejemplo: misma categoría, mismo monto y misma descripción en el mismo día
        return transactionRepository.existsByUserAndCategoryIdAndAmountAndDescriptionAndDate(
                user,
                dtoTransaction.getCategoryId(),
                dtoTransaction.getAmount(),
                dtoTransaction.getDescription(),
                LocalDate.now()
        );
    }

    private ResponseEntity<?> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(DtoResponse.builder()
                        .success(false)
                        .message(message)
                        .build());
    }
    public ResponseEntity<?> validateTransactionDeletion(String token, Long transactionId) {
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

        // Verificar que la transacción exista
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);
        if (optionalTransaction.isEmpty()) {
            return buildErrorResponse("Transacción no encontrada", HttpStatus.NOT_FOUND);
        }

        // Verificar que el usuario sea el dueño de la transacción
        if (!optionalTransaction.get().getUser().getId().equals(user.getId())) {
            return buildErrorResponse("No autorizado para eliminar esta transacción", HttpStatus.FORBIDDEN);
        }

        return null;
    }

    public ResponseEntity<?> validateTransactionUpdate(DtoTransaction dtoTransaction, User user, Category category, Long transactionId) {
        // Validación básica
        if (dtoTransaction == null) {
            return buildErrorResponse("Datos de la transacción son requeridos", HttpStatus.BAD_REQUEST);
        }

        if (dtoTransaction.getAmount() == null || dtoTransaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return buildErrorResponse("Monto inválido", HttpStatus.BAD_REQUEST);
        }

        // Validación de categoría fija
        if (category.isFixed() && (category.getUser() == null || !category.getUser().getId().equals(user.getId()))) {
            return buildErrorResponse("No autorizado para usar esta categoría", HttpStatus.FORBIDDEN);
        }

        // Validación de duplicados (ignorando la transacción actual)
        boolean existsDuplicate = transactionRepository.existsByUserAndCategoryAndAmountAndDescriptionAndIdNot(
                user, category, dtoTransaction.getAmount(), dtoTransaction.getDescription(), transactionId
        );

        if (existsDuplicate) {
            return buildErrorResponse("Ya existe otra transacción con los mismos datos", HttpStatus.CONFLICT);
        }

        return null; // Sin errores
    }

}
