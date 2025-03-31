package com.planify.app.servicies;

import com.planify.app.dtos.DtoLogin;
import com.planify.app.dtos.DtoRegister;
import com.planify.app.dtos.UserResponseDTO;
import com.planify.app.models.User;
import com.planify.app.repositories.UserRepository;
import com.planify.app.security.JwtGenerador;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtGenerador jwtGenerador;


    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtGenerador jwtGenerador) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtGenerador = jwtGenerador;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public ResponseEntity<?> registerUser(DtoRegister userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El correo ya está en uso");
        }

        // Crear la entidad User y asignar valores desde el DTO
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // Encriptar la contraseña
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setPhoneNumber(userDTO.getPhoneNumber());

        User savedUser = userRepository.save(user);

        return new ResponseEntity<>("Registro de usuario exitoso", HttpStatus.OK);
    }

    public ResponseEntity<UserResponseDTO> login(DtoLogin dtoLogin){
        Authentication authentication= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dtoLogin.getEmail(),dtoLogin.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtGenerador.generarToken(authentication);
        return new ResponseEntity<>(new UserResponseDTO(token),HttpStatus.OK);
    }
}
