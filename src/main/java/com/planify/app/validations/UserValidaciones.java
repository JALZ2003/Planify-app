package com.planify.app.validations;

import com.planify.app.dtos.DtoUser;

import java.time.LocalDate;
import java.time.Period;

public class UserValidaciones {

    public static String validateUpdateInput(DtoUser dto) {
        if (dto.getEmail() != null && !dto.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            return "El correo electrónico no tiene un formato válido.";
        }

        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().matches("^\\d{10}$")) {
            return "El número de teléfono debe contener exactamente 10 dígitos.";
        }

        if (dto.getName() != null && dto.getName().trim().isEmpty()) {
            return "El nombre no puede estar vacío.";
        }

        if (dto.getDateOfBirth() != null) {
            LocalDate birthDate = dto.getDateOfBirth();
            LocalDate today = LocalDate.now();

            if (birthDate.isAfter(today)) {
                return "La fecha de nacimiento no puede ser una fecha futura.";
            }

            int age = Period.between(birthDate, today).getYears();
            if (age < 18) {
                return "Debes tener al menos 18 años.";
            }
        }

        return null;
    }
}
