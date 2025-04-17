package com.planify.app.controllers;

import com.planify.app.dtos.DtoLogin;
import com.planify.app.dtos.DtoRegister;
import com.planify.app.dtos.DtoResponse;
import com.planify.app.dtos.UserResponseDTO;
import com.planify.app.models.User;
import com.planify.app.servicies.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody DtoRegister userDTO) {
        return userService.registerUser(userDTO);
    }

    @PostMapping("/login/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        String idToken = request.get("idToken");
        return userService.loginWithGoogle(idToken);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DtoLogin dtoLogin) {
        return userService.login(dtoLogin);
    }
}
