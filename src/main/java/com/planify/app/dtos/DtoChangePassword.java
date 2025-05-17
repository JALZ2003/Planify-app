package com.planify.app.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DtoChangePassword {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
