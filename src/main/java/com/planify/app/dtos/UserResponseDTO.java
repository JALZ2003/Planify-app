package com.planify.app.dtos;

import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String accessToken;
    private String tokenType = "Bearer ";

    public UserResponseDTO(Long id, String email, String accessToken) {
        this.id = id;
        this.email = email;
        this.accessToken = accessToken;
    }

    public UserResponseDTO(String token) {
    }
}
