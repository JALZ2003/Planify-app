package com.planify.app.repositories;

import com.planify.app.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    boolean existsByUserIdAndNameContainingIgnoreCase(Long userId, String name);

}
