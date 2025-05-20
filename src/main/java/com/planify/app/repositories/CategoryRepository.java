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

    List<Category> findByUser(User user);

    // Método para encontrar categorías fijas del sistema (isFixed = false)
    List<Category> findByIsFixedFalse();

    // Método para encontrar categorías del usuario que sean fijas (isFixed = true)
    List<Category> findByUserAndIsFixed(User user, boolean isFixed);

    Optional<Category> findByNameAndUser(String name, User user);
}
