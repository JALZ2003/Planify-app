package com.planify.app.controllers;

import com.planify.app.dtos.*;
import com.planify.app.servicies.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/V1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody DtoRegister userDTO) {
        return userService.registerUser(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody DtoLogin dtoLogin) {

        return userService.login(dtoLogin);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7); // Eliminar "Bearer " del token
        return userService.getUserProfile(jwtToken);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestHeader("Authorization") String token,
                                               @RequestBody DtoUser dtoUser){
        return userService.updateUserProfile(token,dtoUser);

    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody DtoChangePassword dtoChangePassword) {

        return userService.changePassword(token, dtoChangePassword);
    }

}
