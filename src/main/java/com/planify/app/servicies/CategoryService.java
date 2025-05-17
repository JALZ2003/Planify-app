package com.planify.app.servicies;

import com.planify.app.dtos.DtoCategory;
import com.planify.app.dtos.DtoResponse;
import com.planify.app.models.Category;
import com.planify.app.models.FlowType;
import com.planify.app.models.User;
import com.planify.app.repositories.CategoryRepository;
import com.planify.app.repositories.FlowTypeRepository;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
/*
Modificar algunas cositas como no separar (juntar en el mismo enpoint) las categorias definidad
con las del usuario
validar si el user_id tiene algo pertence al usuario que esta haciendo la peticicn
el is_fixed validar que me traiga todos que esten el false y traer los que pertenezca al userid
y que el is_fixed sea true

tener en cuenta que la tabla categoria solo se modifica si el usuario crea una nueva personalizada
 */

@Service
public class CategoryService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlowTypeRepository flowTypeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtGenerador jwtGenerador;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

   public List<FlowType> getAllFlowTypes() {
        return flowTypeRepository.findAll();
    }
    public ResponseEntity<FlowType> getFlowTypeById(Long id){
        Optional<FlowType> flowType = flowTypeRepository.findById(id);

        return flowType.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());

    }

    public ResponseEntity<?> getCombinedCategories(String token, Long userId, Boolean isFixed) {
        try {
            // Validación del token y usuario
            if (token == null || token.isBlank()) {
                return buildErrorResponse("Token no proporcionado", HttpStatus.BAD_REQUEST);
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String idUserFromToken = jwtGenerador.extractId(token);

            // Validar que el userId solicitado coincide con el del token
            if (userId != null && !idUserFromToken.equals(userId.toString())) {
                return buildErrorResponse("No autorizado para acceder a categorías de otro usuario",
                        HttpStatus.FORBIDDEN);
            }

            // Obtener usuario
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(idUserFromToken));
            if (optionalUser.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            User user = optionalUser.get();

            // Lógica para obtener categorías según los filtros
            List<Category> categories = new ArrayList<>();

            if (isFixed == null) {
                // Si no se especifica isFixed, traer todas las categorías
                categories.addAll(categoryRepository.findByIsFixedFalse()); // Categorías fijas del sistema
                categories.addAll(categoryRepository.findByUser(user)); // Categorías del usuario
            } else if (!isFixed) {
                // Solo categorías fijas del sistema (isFixed = false)
                categories.addAll(categoryRepository.findByIsFixedFalse());
            } else {
                // Solo categorías del usuario (isFixed = true)
                categories.addAll(categoryRepository.findByUserAndIsFixed(user, true));
            }

            // Eliminar duplicados y mapear a DTO
            List<DtoCategory> dtoList = categories.stream()
                    .distinct()
                    .map(cat -> DtoCategory.builder()
                            .id(cat.getId())
                            .name(cat.getName())
                            .isFixed(cat.isFixed())
                            .userId(cat.getUser() != null ? cat.getUser().getId() : null)
                            .build())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .message("Categorías")
                    .response(dtoList)
                    .build());

        } catch (Exception e) {
            return buildErrorResponse("Error interno: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<DtoResponse> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(
                DtoResponse.builder()
                        .success(false)
                        .message(message)
                        .response(null)
                        .build()
        );
    }

    public ResponseEntity<?> createUserCategory(String token, DtoCategory dtoCategory) {
        try {
            // Validación del token
            if (token == null || token.isBlank()) {
                return buildErrorResponse("Token no proporcionado", HttpStatus.BAD_REQUEST);
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validar que el token sea válido (no vacío)
            if (token.isBlank()) {
                return buildErrorResponse("Token inválido", HttpStatus.BAD_REQUEST);
            }

            String idUserFromToken = jwtGenerador.extractId(token);

            // Validar datos de entrada del DTO
            if (dtoCategory == null) {
                return buildErrorResponse("Los datos de la categoría son requeridos", HttpStatus.BAD_REQUEST);
            }

            // Validar nombre de categoría
            if (dtoCategory.getName() == null || dtoCategory.getName().trim().isEmpty()) {
                return buildErrorResponse("El nombre de la categoría es requerido", HttpStatus.BAD_REQUEST);
            }

            // Validar que el nombre no contenga solo espacios en blanco
            if (dtoCategory.getName().trim().isEmpty()) {
                return buildErrorResponse("El nombre de la categoría no puede estar vacío", HttpStatus.BAD_REQUEST);
            }

            // Validar longitud del nombre
            if (dtoCategory.getName().length() > 100) {
                return buildErrorResponse("El nombre de la categoría no puede exceder los 100 caracteres", HttpStatus.BAD_REQUEST);
            }

            // Validar flowTypeId si está presente
            if (dtoCategory.getFlowTypeId() != null && dtoCategory.getFlowTypeId() <= 0) {
                return buildErrorResponse("ID de tipo de flujo inválido", HttpStatus.BAD_REQUEST);
            }

            // Obtener usuario
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(idUserFromToken));
            if (optionalUser.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            User user = optionalUser.get();

            // Verificar si la categoría ya existe para este usuario
            Optional<Category> existingCategory = categoryRepository.findByNameAndUser(dtoCategory.getName().trim(), user);
            if (existingCategory.isPresent()) {
                return buildErrorResponse("Ya existe una categoría con este nombre para el usuario", HttpStatus.CONFLICT);
            }

            // Crear nueva categoría
            Category newCategory = new Category();
            newCategory.setName(dtoCategory.getName().trim()); // Eliminar espacios en blanco al inicio/fin
            newCategory.setFixed(true);
            newCategory.setUser(user);

            // Asignar tipo de flujo si se especifica y es válido
            if (dtoCategory.getFlowTypeId() != null) {
                Optional<FlowType> flowType = flowTypeRepository.findById(dtoCategory.getFlowTypeId());
                if (flowType.isEmpty()) {
                    return buildErrorResponse("Tipo de flujo no encontrado", HttpStatus.NOT_FOUND);
                }
                newCategory.setFlowType(flowType.get());
            }

            // Guardar la categoría
            Category savedCategory = categoryRepository.save(newCategory);

            // Construir DTO de respuesta
            DtoCategory responseDto = DtoCategory.builder()
                    .id(savedCategory.getId())
                    .name(savedCategory.getName())
                    .isFixed(savedCategory.isFixed())
                    .userId(savedCategory.getUser().getId())
                    .flowTypeId(savedCategory.getFlowType() != null ?
                            savedCategory.getFlowType().getId() : null)
                    .flowTypeName(savedCategory.getFlowType() != null ?
                            savedCategory.getFlowType().getName() : null) // Añadido el nombre
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DtoResponse.builder()
                            .success(true)
                            .message("Categoría creada exitosamente")
                            .response(responseDto)
                            .build());

        } catch (NumberFormatException e) {
            return buildErrorResponse("Formato de ID de usuario inválido", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error al crear categoría: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<?> updateUserCategory(String token, Long categoryId, DtoCategory dtoCategory) {
        try {
            // Validación del token
            if (token == null || token.isBlank()) {
                return buildErrorResponse("Token no proporcionado", HttpStatus.BAD_REQUEST);
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String idUserFromToken = jwtGenerador.extractId(token);

            // Validar datos de entrada
            if (dtoCategory.getName() == null || dtoCategory.getName().isBlank()) {
                return buildErrorResponse("El nombre de la categoría es requerido", HttpStatus.BAD_REQUEST);
            }

            // Obtener usuario
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(idUserFromToken));
            if (optionalUser.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            User user = optionalUser.get();

            // Buscar la categoría a actualizar
            Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
            if (optionalCategory.isEmpty()) {
                return buildErrorResponse("Categoría no encontrada", HttpStatus.NOT_FOUND);
            }

            Category category = optionalCategory.get();

            // Verificar que la categoría pertenece al usuario
            if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
                return buildErrorResponse("No tienes permiso para modificar esta categoría", HttpStatus.FORBIDDEN);
            }

            // Verificar si el nuevo nombre ya existe para este usuario
            if (!category.getName().equals(dtoCategory.getName())) {
                Optional<Category> existingCategory = categoryRepository.findByNameAndUser(dtoCategory.getName(), user);
                if (existingCategory.isPresent()) {
                    return buildErrorResponse("Ya existe una categoría con este nombre para el usuario", HttpStatus.CONFLICT);
                }
            }

            // Actualizar la categoría
            category.setName(dtoCategory.getName());

            // Actualizar tipo de flujo si se especifica
            if (dtoCategory.getFlowTypeId() != null) {
                Optional<FlowType> flowType = flowTypeRepository.findById(dtoCategory.getFlowTypeId());
                flowType.ifPresentOrElse(
                        category::setFlowType,
                        () -> category.setFlowType(null) // Si no existe, lo removemos
                );
            } else {
                category.setFlowType(null);
            }

            // Guardar los cambios
            Category updatedCategory = categoryRepository.save(category);

            // Construir DTO de respuesta
            DtoCategory responseDto = DtoCategory.builder()
                    .id(updatedCategory.getId())
                    .name(updatedCategory.getName())
                    .isFixed(updatedCategory.isFixed())
                    .flowTypeId(updatedCategory.getFlowType() != null ?
                            updatedCategory.getFlowType().getId() : null)
                    .flowTypeName(updatedCategory.getFlowType() != null ?
                            updatedCategory.getFlowType().getName() : null)
                    .userId(updatedCategory.getUser().getId())
                    .build();

            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .message("Categoría actualizada exitosamente")
                    .response(responseDto)
                    .build());

        } catch (Exception e) {
            return buildErrorResponse("Error al actualizar categoría: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteUserCategory(String token, Long categoryId) {
        try {
            // Validación del token
            if (token == null || token.isBlank()) {
                return buildErrorResponse("Token no proporcionado", HttpStatus.BAD_REQUEST);
            }

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            String idUserFromToken = jwtGenerador.extractId(token);

            // Obtener usuario
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(idUserFromToken));
            if (optionalUser.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
            User user = optionalUser.get();

            // Buscar la categoría a eliminar
            Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
            if (optionalCategory.isEmpty()) {
                return buildErrorResponse("Categoría no encontrada", HttpStatus.NOT_FOUND);
            }

            Category category = optionalCategory.get();

            // Verificar que la categoría pertenece al usuario
            if (category.getUser() == null || !category.getUser().getId().equals(user.getId())) {
                return buildErrorResponse("No tienes permiso para eliminar esta categoría", HttpStatus.FORBIDDEN);
            }

            // Eliminar la categoría
            categoryRepository.delete(category);

            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .message("Categoría eliminada exitosamente")
                    .response(null)
                    .build());

        } catch (Exception e) {
            return buildErrorResponse("Error al eliminar categoría: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
