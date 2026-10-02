package com.bloodbank.repository;

import com.bloodbank.model.BloodStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodStockRepository extends JpaRepository<BloodStock, String> {
}
