package com.planify.app.repositories;

import com.planify.app.models.FlowType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FlowTypeRepository extends JpaRepository<FlowType,Long> {

    Optional<FlowType> findByName(String name);
}
