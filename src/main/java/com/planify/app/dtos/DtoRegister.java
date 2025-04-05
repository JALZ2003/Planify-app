package com.planify.app.dtos;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class DtoRegister {

    private String name;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private String phoneNumber;
}
