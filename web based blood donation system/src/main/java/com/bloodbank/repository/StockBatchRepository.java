package com.bloodbank.repository;

import com.bloodbank.model.StockBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockBatchRepository extends JpaRepository<StockBatch, String> {
}
