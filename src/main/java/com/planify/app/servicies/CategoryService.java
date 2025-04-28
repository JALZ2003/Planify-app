package com.planify.app.servicies;

import com.planify.app.dtos.DtoCategory;
import com.planify.app.dtos.DtoResponse;
import com.planify.app.dtos.DtoUser;
import com.planify.app.models.Category;
import com.planify.app.repositories.CategoryRepository;
import com.planify.app.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private JwtGenerador  jwtGenerador;
    @Autowired
    private CategoryRepository categoryRepository;

    public ResponseEntity<?> createdCategory(String token, DtoCategory dtoCategory) {

        try {
            if (token.startsWith("Bearer")) {
                token = token.substring(7);
            }
            String idUser = jwtGenerador.extractId(token);
            Optional<Category> optionalCategory = categoryRepository.findById(Long.parseLong(idUser));

            if(optionalCategory.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrada");
            }

            Category category = optionalCategory.get();

            category.setName(dtoCategory.getName());
            category.setId(dtoCategory.getUserId().getId());
            category.setFixed(dtoCategory.isFixed());

            categoryRepository.save(category);

            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .message("Categoria creada")
                    .response(null)
                    .build());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalido o datos incorrectos.");
        }
    }
}