package com.planify.app.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DtoResponse {

    private boolean success;
    private Object response;
    private String message;

}
