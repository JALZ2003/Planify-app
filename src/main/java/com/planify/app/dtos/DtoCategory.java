package com.planify.app.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DtoCategory {

    private Long id;
    private String name;
    private boolean isFixed;
    private Long flowTypeId;
    private String flowTypeName;  // opcional, si aún necesitas el nombre en otros casos
    private Long userId;


}
