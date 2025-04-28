package com.planify.app.dtos;

import com.planify.app.models.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DtoCategory {

    private String name;

    private User userId;

    private boolean isFixed;

}
