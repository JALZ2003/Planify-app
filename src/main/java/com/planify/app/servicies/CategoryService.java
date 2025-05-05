package com.planify.app.servicies;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    /*
    @Autowired
private JwtGenerador jwtGenerador;
@Autowired
private CategoryRepository categoryRepository;
@Autowired
private UserRepository userRepository;

@Autowired
private FlowTypeRespository typeRespository;
@Autowired
private UserDetailsService userDetailsService;

public ResponseEntity<?> createdCategory(DtoCategory dtoCategory) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.isAuthenticated()) {
        return ResponseEntity.status(401).body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message("Token no valido")
                .build());
    }
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
    if (!user.isPresent()) {
        return ResponseEntity.status(404).body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message("User not found!!")
                .build());
    }

    // Verificar existencia usando flowTypeId
    boolean exists = categoryRepository.existsByUserIdAndNameContainingIgnoreCaseAndFlowTypeId(
            user.get().getId(),
            dtoCategory.getName(),
            dtoCategory.getFlowTypeId()
    );

    if (exists) {
        return ResponseEntity.status(400).body(DtoResponse.builder()
                .success(false)
                .message("La categoría ya existe para este usuario y tipo")
                .build());
    }

    // Obtener FlowType desde la base de datos
    FlowType flowType = typeRespository.findById(dtoCategory.getFlowTypeId())
            .orElseThrow(() -> new RuntimeException("Tipo de flujo no encontrado"));

    Category category = Category.builder()
            .name(dtoCategory.getName())
            .user(user.get())
            .isFixed(dtoCategory.isFixed())
            .flowType(flowType)
            .build();

    Category saved = categoryRepository.save(category);

    // Respuesta con DTO actualizado
    DtoCategory responseDto = DtoCategory.builder()
            .id(saved.getId())
            .name(saved.getName())
            .isFixed(saved.isFixed())
            .flowTypeId(saved.getFlowType().getId())
            .flowTypeName(saved.getFlowType().getName())
            .build();

    return ResponseEntity.ok(DtoResponse.builder()
            .success(true)
            .response(responseDto)
            .message("Categoría creada")
            .build());
}

public ResponseEntity<?> getCategoryById(Long idCategory) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.isAuthenticated()) {
        return ResponseEntity.status(401).body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message("Token no válido")
                .build());
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
    if (!user.isPresent()) {
        return ResponseEntity.status(404).body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message("Usuario no encontrado")
                .build());
    }

    // Buscar categoría por ID y ID de usuario (seguridad)
    Optional<Category> optionalCategory = categoryRepository.findByIdAndUserId(idCategory, user.get().getId());

    if (!optionalCategory.isPresent()) {
        return ResponseEntity.status(404).body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message("Categoría no encontrada para este usuario")
                .build());
    }

   Category category = categoryRepository.findByIdAndUserId(idCategory, user.get().getId())
            .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

    DtoCategory dto = DtoCategory.builder()
            .id(category.getId())
            .name(category.getName())
            .isFixed(category.isFixed())
            .flowTypeId(category.getFlowType().getId())
            .flowTypeName(category.getFlowType().getName())
            .build();

    return ResponseEntity.ok(DtoResponse.builder()
            .success(true)
            .response(dto)
            .build());
}


public ResponseEntity<?> getAllCategoriesByUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.isAuthenticated()) {
        return ResponseEntity.status(401).body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message("Token no válido")
                .build());
    }

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
    if (!user.isPresent()) {
        return ResponseEntity.status(404).body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message("Usuario no encontrado")
                .build());
    }

    List<DtoCategory> dtos = categoryRepository.findByUserId(user.get().getId())
            .stream()
            .map(c -> DtoCategory.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .isFixed(c.isFixed())
                    .flowTypeId(c.getFlowType().getId())
                    .flowTypeName(c.getFlowType().getName())
                    .build())
            .collect(Collectors.toList());

    return ResponseEntity.ok(DtoResponse.builder()
            .success(true)
            .response(dtos)
            .build());
}

public ResponseEntity<?> getCategoriesByFlowType(Long flowTypeId) {  // Cambiado a Long flowTypeId
    // 1. Validar autenticación
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (!authentication.isAuthenticated()) {
        return ResponseEntity.status(401).body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message("Token no válido")
                .build());
    }

    // 2. Obtener usuario
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
    if (!user.isPresent()) {
        return ResponseEntity.status(404).body(DtoResponse.builder()
                .success(false)
                .response(null)
                .message("Usuario no encontrado")
                .build());
    }

   // Verificar que el flowType exista
    FlowType flowType = typeRespository.findById(flowTypeId)
            .orElseThrow(() -> new RuntimeException("Tipo de flujo no encontrado"));

    List<DtoCategory> dtos = categoryRepository.findByUserIdAndFlowTypeId(
                    user.get().getId(),
                    flowTypeId)
            .stream()
            .map(c -> DtoCategory.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .isFixed(c.isFixed())
                    .flowTypeId(flowTypeId)
                    .flowTypeName(flowType.getName())
                    .build())
            .collect(Collectors.toList());

    return ResponseEntity.ok(DtoResponse.builder()
            .success(true)
            .response(dtos)
            .build());
}
Respository:
boolean existsByUserIdAndNameContainingIgnoreCaseAndFlowType(
        Long userId,
        String name,
        FlowType flowType
);
// Para getAllCategoriesByUser
List<Category> findByUserId(Long userId);

boolean existsByUserIdAndNameContainingIgnoreCaseAndFlowTypeId(Long userId, String name, Long flowTypeId);

// Para getCategoriesByFlowType
List<Category> findByUserIdAndFlowTypeId(Long userId, Long flowTypeId);

     */

}