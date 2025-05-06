package com.planify.app.repositories;

import com.planify.app.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    boolean existsByUserIdAndNameContainingIgnoreCase(Long userId, String name);

    List<Category> findAllByUserId(Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    // Método corregido para verificar existencia
    boolean existsByUserIdAndNameContainingIgnoreCaseAndFlowType_Id( Long userId, String name, Long flowTypeId );

}
