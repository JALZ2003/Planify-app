package com.planify.app.servicies;

import com.planify.app.dtos.DtoResponse;
import com.planify.app.dtos.DtoTransaction;
import com.planify.app.models.Category;
import com.planify.app.models.Transaction;
import com.planify.app.models.User;
import com.planify.app.repositories.CategoryRepository;
import com.planify.app.repositories.FlowTypeRepository;
import com.planify.app.repositories.TransactionRepository;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtGenerador jwtGenerador;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TransactionRepository transactionRepository;

    private ResponseEntity<?> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(DtoResponse.builder()
                        .success(false)
                        .message(message)
                        .build());
    }

    public ResponseEntity<?> createTransaction(String token, DtoTransaction dtoTransaction) {
        try {
            if (token == null || token.isBlank()) {
                return buildErrorResponse("Token no proporcionado", HttpStatus.BAD_REQUEST);
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String idUserFromToken = jwtGenerador.extractId(token);
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(idUserFromToken));
            if (optionalUser.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            User user = optionalUser.get();

            if (dtoTransaction == null) {
                return buildErrorResponse("Datos de la transacción son requeridos", HttpStatus.BAD_REQUEST);
            }

            if (dtoTransaction.getAmount() == null || dtoTransaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return buildErrorResponse("Monto inválido", HttpStatus.BAD_REQUEST);
            }

            if (dtoTransaction.getCategoryId() == null) {
                return buildErrorResponse("La categoría es requerida", HttpStatus.BAD_REQUEST);
            }

            // Obtener categoría
            Optional<Category> optionalCategory = categoryRepository.findById(dtoTransaction.getCategoryId());
            if (optionalCategory.isEmpty()) {
                return buildErrorResponse("Categoría no encontrada", HttpStatus.NOT_FOUND);
            }
            Category category = optionalCategory.get();

            // Validar permisos sobre categoría
            if (category.isFixed()) {
                // Categoría personalizada, debe ser del usuario
                if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
                    return buildErrorResponse("No autorizado para usar esta categoría", HttpStatus.FORBIDDEN);
                }
            }
            // Si isFixed == false (categoría global), cualquier usuario puede usarla

            // Crear transacción
            Transaction transaction = Transaction.builder()
                    .user(user)
                    .category(category)
                    .amount(dtoTransaction.getAmount())
                    .description(dtoTransaction.getDescription())
                    .date(LocalDate.now())
                    .build();

            Transaction savedTransaction = transactionRepository.save(transaction);

            // Puedes armar un DTOResponse con la transacción creada o solo devolver mensaje
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    DtoResponse.builder()
                            .success(true)
                            .message("Transacción creada exitosamente")
                            .response(savedTransaction.getId()) // enviar solo el id de la transaccion creada
                            .build()
            );

        } catch (NumberFormatException e) {
            return buildErrorResponse("Formato de ID de usuario inválido", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al crear transacción: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

