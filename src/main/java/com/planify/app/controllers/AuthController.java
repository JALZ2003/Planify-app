package com.planify.app.controllers;

import com.planify.app.dtos.DtoLogin;
import com.planify.app.dtos.DtoRegister;
import com.planify.app.dtos.UserResponseDTO;
import com.planify.app.models.User;
import com.planify.app.servicies.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody DtoRegister userDTO) {
        return userService.registerUser(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody DtoLogin dtoLogin) {
        return userService.login(dtoLogin);
    }
}
