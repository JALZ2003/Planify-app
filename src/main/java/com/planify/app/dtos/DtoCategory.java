package com.planify.app.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DtoCategory {

    private Long id;
    private String name;
    private boolean isFixed;
    private Long flowTypeId;          // Cambiado a ID
    private String flowTypeName;

}
