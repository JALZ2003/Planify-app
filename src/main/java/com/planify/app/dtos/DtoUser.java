package com.planify.app.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Data
public class DtoUser {
    private Long id;
    private String name;
    private String email;
    //private String password;
    private LocalDate dateOfBirth;
    private String phoneNumber;
}
