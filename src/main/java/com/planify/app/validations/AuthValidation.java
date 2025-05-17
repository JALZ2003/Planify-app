package com.planify.app.validations;

import com.planify.app.dtos.DtoChangePassword;
import com.planify.app.dtos.DtoLogin;
import com.planify.app.dtos.DtoRegister;
import com.planify.app.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.Period;

public class AuthValidation {
    // Constantes para mensajes de error
    private static final String PASSWORD_ERROR = "La contraseña debe tener al menos 8 caracteres.";
    private static final String EMAIL_ERROR = "El correo electrónico no tiene un formato válido.";
    private static final String PHONE_ERROR = "El número de teléfono debe contener exactamente 10 dígitos.";
    private static final String BIRTHDATE_REQUIRED = "La fecha de nacimiento es requerida.";
    private static final String FUTURE_DATE_ERROR = "La fecha de nacimiento no puede ser futura.";
    private static final String MINIMUM_AGE_ERROR = "Debes tener al menos 18 años para registrarte.";

    // Patrones de validación
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PHONE_REGEX = "^\\d{10}$";
    private static final int MINIMUM_AGE = 18;

    public static String validateRegisterInput(DtoRegister dto) {
        // Validación de contraseña
        if (dto.getPassword() == null || dto.getPassword().length() < 8) {
            return PASSWORD_ERROR;
        }

        // Validación de email
        if (dto.getEmail() == null || !dto.getEmail().matches(EMAIL_REGEX)) {
            return EMAIL_ERROR;
        }

        // Validación de teléfono
        if (dto.getPhoneNumber() == null || !dto.getPhoneNumber().matches(PHONE_REGEX)) {
            return PHONE_ERROR;
        }

        // Validación de fecha de nacimiento
        if (dto.getDateOfBirth() == null) {
            return BIRTHDATE_REQUIRED;
        }

        LocalDate today = LocalDate.now();
        if (dto.getDateOfBirth().isAfter(today)) {
            return FUTURE_DATE_ERROR;
        }

        if (Period.between(dto.getDateOfBirth(), today).getYears() < MINIMUM_AGE) {
            return MINIMUM_AGE_ERROR;
        }

        return null; // Todas las validaciones pasaron
    }

    public static String validateLoginInput(DtoLogin dto) {
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            return "El correo electrónico es requerido";
        }

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            return "La contraseña es requerida";
        }

        return null;
    }

    public static String validateChangePasswordInput(DtoChangePassword dtoChangePassword, User user, BCryptPasswordEncoder passwordEncoder) {
        // Validar nueva contraseña
        if (dtoChangePassword.getNewPassword() == null || dtoChangePassword.getNewPassword().isEmpty()) {
            return "La nueva contraseña no puede estar vacía";
        }

        // Validar coincidencia de contraseñas
        if (!dtoChangePassword.getNewPassword().equals(dtoChangePassword.getConfirmPassword())) {
            return "Las contraseñas nuevas no coinciden";
        }

        // Validar contraseña actual para usuarios no Google
        if (user.getPassword() != null) {
            if (dtoChangePassword.getCurrentPassword() == null || dtoChangePassword.getCurrentPassword().isEmpty()) {
                return "La contraseña actual es requerida";
            }

            if (!passwordEncoder.matches(dtoChangePassword.getCurrentPassword(), user.getPassword())) {
                return "La contraseña actual es incorrecta";
            }
        }

        // Validar que la nueva sea diferente a la actual
        if (user.getPassword() != null && passwordEncoder.matches(dtoChangePassword.getNewPassword(), user.getPassword())) {
            return "La nueva contraseña debe ser diferente a la actual";
        }

        // Validar fortaleza de la nueva contraseña
        String passwordError = validatePassword(dtoChangePassword.getNewPassword());
        if (passwordError != null) {
            return passwordError;
        }

        return null;
    }

    public static String validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }

        // Validar mayúsculas
        if (!password.matches(".*[A-Z].*")) {
            return "La contraseña debe contener al menos una mayúscula";
        }

        // Validar minúsculas
        if (!password.matches(".*[a-z].*")) {
            return "La contraseña debe contener al menos una minúscula";
        }

        // Validar números
        if (!password.matches(".*\\d.*")) {
            return "La contraseña debe contener al menos un número";
        }

        // Validar caracteres especiales
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            return "La contraseña debe contener al menos un carácter especial";
        }

        return null;
    }
}
