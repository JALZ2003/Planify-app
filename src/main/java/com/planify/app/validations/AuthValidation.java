package com.planify.app.validations;

import com.planify.app.dtos.DtoLogin;
import com.planify.app.dtos.DtoRegister;

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
}
