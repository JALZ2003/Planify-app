package com.planify.app.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class DtoScheduledSaving {

    private Long id;
    private BigDecimal amount;
    private LocalDate startDate;
    private String name;
    private BigDecimal goalAmount;

}
