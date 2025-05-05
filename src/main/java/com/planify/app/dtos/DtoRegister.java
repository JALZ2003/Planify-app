package com.planify.app.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class DtoRegister {

    @NotBlank(message = "Nombre es requerido")
    @NotNull(message = "Campos requeridos")
    private String name;
    @Email(message = "Email es requerido")
    @NotBlank(message = "Email es requerido")
    @NotNull(message = "Campos requeridos")
    private String email;
    @Size(min = 8 , message = "La contraeña debe tener al menos 8 caracteres")
    @NotNull(message = "Campos requeridos")
    private String password;
    @NotNull(message = "Campos requeridos")
    @Past(message = "Fecha nacimiento valida")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    @NotNull(message = "Campos requeridos")
    @Size(min = 10,message = "debe de ser minimo de 10 digitos")
    private String phoneNumber;
}
