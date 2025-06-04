package com.planify.app.repositories;

import com.planify.app.models.Category;
import com.planify.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    List<Category> findByUserIdOrIsFixedFalse(Long userId);

    List<Category> findByUser(User user);

    // Método para encontrar categorías fijas del sistema (isFixed = false)
    List<Category> findByIsFixedFalse();

    // Método para encontrar categorías del usuario que sean fijas (isFixed = true)
    List<Category> findByUserAndIsFixed(User user, boolean isFixed);

     // Categorías del sistema: isFixed = false + user = null
    List<Category> findByIsFixedFalseAndUserIsNull();

    // Categorías del usuario: isFixed = true + user.id = userId
    List<Category> findByIsFixedTrueAndUserId(Long userId);

    // CategoryRepository.java
    List<Category> findByUserIdOrIsFixedTrue(Long userId);

     List<Category> findByUserIdOrIsFixedTrueOrUserIdIsNull(Long userId);

    Optional<Category> findByNameAndUser(String name, User user);
}
