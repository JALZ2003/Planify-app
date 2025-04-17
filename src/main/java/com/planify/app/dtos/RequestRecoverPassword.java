package com.planify.app.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestRecoverPassword {

    private String email;
    private String code;
    private String newPassword;
    private String phone;

}
