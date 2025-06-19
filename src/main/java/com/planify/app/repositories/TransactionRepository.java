package com.planify.app.repositories;

import com.planify.app.models.Category;
import com.planify.app.models.Transaction;
import com.planify.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUser(User user);

    List<Transaction> findByUserIdAndDate(Long userId, LocalDate date);


    Optional<Transaction> findByIdAndUserId(Long id, Long userId);


    boolean existsByUserAndCategoryIdAndAmountAndDescriptionAndDate(
            User user,
            Long categoryId,
            BigDecimal amount,
            String description,
            LocalDate date
    );

    boolean existsByUserAndCategoryAndAmountAndDescriptionAndIdNot(
            User user,
            Category category,
            BigDecimal amount,
            String description,
            Long id
    );
}
