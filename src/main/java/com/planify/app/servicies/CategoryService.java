package com.planify.app.servicies;

import com.planify.app.dtos.DtoCategory;
import com.planify.app.dtos.DtoResponse;
import com.planify.app.dtos.DtoUser;
import com.planify.app.models.Category;
import com.planify.app.models.User;
import com.planify.app.repositories.CategoryRepository;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private JwtGenerador jwtGenerador;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;
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

        boolean optionalCategory = categoryRepository.existsByUserIdAndNameContainingIgnoreCase(user.get().getId(), dtoCategory.getName());

        if (optionalCategory) {
            return ResponseEntity.status(404).body(DtoResponse.builder()
                    .success(false)
                    .response(null)
                    .message("La categoria ya existe para este usuario!!")
                    .build());
        }

        Category category = Category.builder()
                .name(dtoCategory.getName())
                .user(user.get())
                .isFixed(dtoCategory.isFixed())
                .build();

        Category categorySaved = categoryRepository.save(category);

        return ResponseEntity.ok(DtoResponse.builder()
                .success(true)
                .response(categorySaved.getId())
                .message("Categoria creada con exito!")
                .build());

    }
}