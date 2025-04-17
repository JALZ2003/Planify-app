package com.planify.app.repositories;

import com.planify.app.models.PasswordResetCode;
import com.planify.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {

    Optional<PasswordResetCode> findByUserAndCodeAndUsedIsFalse(User user, String code);
    Optional<PasswordResetCode> findByUserAndCode(User user, String code);

}
