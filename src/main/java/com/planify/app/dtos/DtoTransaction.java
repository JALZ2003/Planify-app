package com.planify.app.dtos;

import com.planify.app.models.Transaction;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
public class DtoTransaction {

    private Long id;
    private BigDecimal amount;
    private Long categoryId;
    private String description;

    // Método estático para conversión desde Transaction
    public static DtoTransaction from(Transaction transaction) {
        DtoTransaction dto = new DtoTransaction();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setCategoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null);
        dto.setDescription(transaction.getDescription());
        return dto;
    }
}
