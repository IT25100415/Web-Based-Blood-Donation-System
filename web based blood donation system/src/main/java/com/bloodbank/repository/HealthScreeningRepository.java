package com.bloodbank.repository;

import com.bloodbank.model.HealthScreening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthScreeningRepository extends JpaRepository<HealthScreening, Long> {
}
