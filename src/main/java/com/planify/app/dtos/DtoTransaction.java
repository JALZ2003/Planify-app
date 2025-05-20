package com.planify.app.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
public class DtoTransaction {

    private BigDecimal amount;
    private Long categoryId;
    private String description;

}
