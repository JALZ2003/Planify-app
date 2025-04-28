package com.planify.app.dtos;
import com.planify.app.models.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class MovementResponseDTO {

    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate date;
    private String description;
    private String categoryName;
}
