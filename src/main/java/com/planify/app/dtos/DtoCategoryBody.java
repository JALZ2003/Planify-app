package com.planify.app.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DtoCategoryBody {

    private String name;
    private boolean isFixed;
    private Long flowTypeId;

}
