package com.planify.app.controllers;

import com.planify.app.dtos.RequestRecoverPassword;
import com.planify.app.servicies.PasswordResetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recover-password")
public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/request-reset")
    public ResponseEntity<?> requestReset(@RequestBody RequestRecoverPassword requestRecoverPassword) {
        return passwordResetService.sendResetCode(requestRecoverPassword.getEmail(), requestRecoverPassword.getPhone());
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody RequestRecoverPassword requestRecoverPassword) {
        return passwordResetService.verifyCode(requestRecoverPassword.getEmail(), requestRecoverPassword.getCode());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody RequestRecoverPassword requestRecoverPassword) {
        return passwordResetService.resetPassword(requestRecoverPassword.getEmail(), requestRecoverPassword.getCode(), requestRecoverPassword.getNewPassword());
    }

}
