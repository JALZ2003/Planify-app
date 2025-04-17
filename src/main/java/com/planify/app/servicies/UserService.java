package com.planify.app.servicies;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.planify.app.dtos.DtoLogin;
import com.planify.app.dtos.DtoRegister;
import com.planify.app.dtos.DtoResponse;
import com.planify.app.dtos.UserResponseDTO;
import com.planify.app.models.User;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private JwtGenerador jwtGenerador;
    @Autowired
    private AuthenticationManager authenticationManager;

    public ResponseEntity<?> registerUser(DtoRegister userDTO) {
        passwordEncoder = new BCryptPasswordEncoder();
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            DtoResponse dtoResponse = DtoResponse.builder()
                    .success(false)
                    .response(null)
                    .message("El usuario ya existe")
                    .build();
            return new ResponseEntity<>(dtoResponse, HttpStatus.UNAUTHORIZED);
        }

        // Crear la entidad User y asignar valores desde el DTO
        User user = User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .dateOfBirth(userDTO.getDateOfBirth())
                .phoneNumber(userDTO.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        DtoResponse dtoResponse = DtoResponse.builder()
                .success(true)
                .response(savedUser.getId())
                .message("Usuario Creado Con exito")
                .build();

        return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> login(DtoLogin dtoLogin) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dtoLogin.getEmail(), dtoLogin.getPassword()));

        Optional<User> user = userRepository.findByEmail(dtoLogin.getEmail());
        String token = jwtGenerador.generarToken(user.get());

        DtoResponse dtoResponse = DtoResponse.builder()
                .success(true)
                .response(UserResponseDTO.builder()
                        .id(user.get().getId())
                        .accessToken(token)
                        .email(user.get().getEmail())
                        .build())
                .message("Inicio de sesion exitoso")
                .build();

        return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
    }

    public ResponseEntity<?> loginWithGoogle(String idToken) {
        try {
            // 1. Verifica el token con Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();

            // 2. Busca o crea el usuario en tu DB
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;

            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                user = User.builder()
                        .email(email)
                        .name(name)
                        .password(null) // Usuario de Google no tiene password
                        .dateOfBirth(null)
                        .phoneNumber(null)
                        .build();
                user = userRepository.save(user);
            }

            // 3. Genera el JWT
            String token = jwtGenerador.generarToken(user);

            // 4. Retorna la respuesta en tu formato estándar
            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .response(UserResponseDTO.builder()
                            .id(user.getId())
                            .accessToken(token)
                            .email(user.getEmail())
                            .build())
                    .message("Login con Google exitoso")
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(401).body(DtoResponse.builder()
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }
}
