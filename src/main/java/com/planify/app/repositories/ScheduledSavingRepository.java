package com.planify.app.repositories;

import com.planify.app.models.ScheduledSaving;
import com.planify.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduledSavingRepository extends JpaRepository<ScheduledSaving, Long> {

    List<ScheduledSaving> findByUserId(Long userId);

    Optional<ScheduledSaving> findByIdAndUserId(Long id, Long userId);
    List<ScheduledSaving> findByUser(User user);

}
