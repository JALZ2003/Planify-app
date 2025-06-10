package com.planify.app.dtos;

import com.planify.app.models.Transaction;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DtoTransactionCategory {

    private Long id;
    private BigDecimal amount;
    private DtoCategory categoryId;
    private String description;

    // Método estático para conversión desde Transaction
    public static DtoTransactionCategory from(Transaction transaction) {
        DtoTransactionCategory transactionCategory = new DtoTransactionCategory();
        transactionCategory.setId(transaction.getId());
        transactionCategory.setAmount(transaction.getAmount());
        transactionCategory.setCategoryId(transaction.getCategory() != null ?
                DtoCategory.builder()
                        .id(transaction.getCategory().getId())
                        .name(transaction.getCategory().getName())
                        .isFixed(transaction.getCategory().isFixed())
                        .flowTypeId(transaction.getCategory().getFlowType().getId())
                        .flowTypeName(transaction.getCategory().getFlowType().getName())
                        .userId(transaction.getCategory().getUser() != null ? transaction.getCategory().getUser().getId() : 0)
                        .build() : null);
        transactionCategory.setDescription(transaction.getDescription());
        return transactionCategory;
    }

}
