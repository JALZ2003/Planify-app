package com.planify.app.repositories;

import com.planify.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phone);

    Optional<User> findById(long id);

    boolean existsByEmail(String email);
}
