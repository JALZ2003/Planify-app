package com.planify.app.servicies;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.planify.app.dtos.*;
import com.planify.app.models.User;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import com.planify.app.validations.AuthValidation;
import com.planify.app.validations.UserValidaciones;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

import static com.planify.app.validations.AuthValidation.validateLoginInput;

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

        // Validaciones
        String validationError = AuthValidation.validateRegisterInput(userDTO);
        if (validationError != null) {
            return buildErrorResponse(validationError, HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return buildErrorResponse("El usuario ya existe", HttpStatus.UNAUTHORIZED);
        }

        User user = User.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .dateOfBirth(userDTO.getDateOfBirth())
                .phoneNumber(userDTO.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(
                DtoResponse.builder()
                        .success(true)
                        .response(savedUser.getId())
                        .message("Usuario creado con éxito")
                        .build()
        );
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



    public ResponseEntity<?> login(DtoLogin dtoLogin) {
        String validationError = validateLoginInput(dtoLogin);
        if (validationError != null) {
            return buildErrorResponse(validationError, HttpStatus.BAD_REQUEST);
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dtoLogin.getEmail(), dtoLogin.getPassword())
            );

            Optional<User> user = userRepository.findByEmail(dtoLogin.getEmail());
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(DtoResponse.builder()
                                .success(false)
                                .message("Usuario no encontrado")
                                .response(null)
                                .build());
            }

            String token = jwtGenerador.generarToken(user.get());

            DtoResponse dtoResponse = DtoResponse.builder()
                    .success(true)
                    .response(UserResponseDTO.builder()
                            .id(user.get().getId())
                            .accessToken(token)
                            .email(user.get().getEmail())
                            .build())
                    .message("Inicio de sesión exitoso")
                    .build();

            return new ResponseEntity<>(dtoResponse, HttpStatus.OK);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(DtoResponse.builder()
                            .success(false)
                            .message("Correo o contraseña incorrectos")
                            .response(null)
                            .build());
        }
    }


    public ResponseEntity<?> loginWithGoogle(String idToken) {
        try {
            // 1. Verifica el token con Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();

            // 2. Busca o crea el usuario en la DB
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

            // 4. Retorna la respuesta en formato estándar
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

    public ResponseEntity<?> getUserProfile(String token){
        try {
            String idUser = jwtGenerador.extractId(token);

            Optional<User> optionalUser = userRepository.findById(Long.parseLong(idUser));
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            User user = optionalUser.get();
            DtoUser userDTO = DtoUser.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .dateOfBirth(user.getDateOfBirth())


                    .build();

            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .message("Perfil del usuario")
                    .response(userDTO)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token inválido o expirado.");
        }
    }

    public ResponseEntity<?> updateUserProfile(String token, DtoUser dtoUser) {
        try {
            if (token.startsWith("Bearer")) {
                token = token.substring(7);
            }

            String idUser = jwtGenerador.extractId(token);
            Optional<User> optionalUser = userRepository.findById(Long.parseLong(idUser));

            if (optionalUser.isEmpty()) {
                return buildErrorResponse("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }

            String validationError = UserValidaciones.validateUpdateInput(dtoUser);
            if (validationError != null) {
                return buildErrorResponse(validationError, HttpStatus.BAD_REQUEST);
            }

            User user = optionalUser.get();

            // Actualizar solo campos no nulos
            if (dtoUser.getName() != null) {
                user.setName(dtoUser.getName());
            }
            if (dtoUser.getEmail() != null) {
                user.setEmail(dtoUser.getEmail());
            }
            if (dtoUser.getDateOfBirth() != null) {
                user.setDateOfBirth(dtoUser.getDateOfBirth());
            }
            if (dtoUser.getPhoneNumber() != null) {
                user.setPhoneNumber(dtoUser.getPhoneNumber());
            }

            userRepository.save(user);

            return ResponseEntity.ok(DtoResponse.builder()
                    .success(true)
                    .message("Perfil actualizado correctamente")
                    .response(null)
                    .build());

        } catch (Exception e) {
            return buildErrorResponse("Token inválido o datos incorrectos.", HttpStatus.BAD_REQUEST);
        }
    }






}
