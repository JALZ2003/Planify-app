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

    List<Transaction> findByUserId(Long userId);


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
    /*boolean existsByUserAndCategoryAndAmountAndDateAndDescription(
            User user,
            Category category,
            BigDecimal amount,
            LocalDate date,
            String description);

    // Versión 2: Validación con ventana de tiempo (JPQL personalizado)
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category = :category " +
            "AND t.amount = :amount " +
            "AND t.date BETWEEN :startDate AND :endDate " +
            "AND (:description IS NULL OR t.description = :description)")
    boolean existsByUserAndCategoryAndAmountAndDateBetweenAndDescription(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("amount") BigDecimal amount,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("description") String description);

    // Versión 3: Validación simplificada (mismo día, monto y categoría)
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category = :category " +
            "AND t.amount = :amount " +
            "AND t.date = :date")
    boolean existsByUserAndCategoryAndAmountAndDate(
            @Param("user") User user,
            @Param("category") Category category,
            @Param("amount") BigDecimal amount,
            @Param("date") LocalDate date);

    boolean existsByUserAndCategoryAndAmountAndCreatedAtBetweenAndDescription(

            User user, Category category, BigDecimal amount, LocalDateTime start, LocalDateTime end, String description);
*/}
