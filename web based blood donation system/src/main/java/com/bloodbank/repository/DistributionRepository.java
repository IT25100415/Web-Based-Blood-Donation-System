package com.bloodbank.repository;

import com.bloodbank.model.Distribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DistributionRepository extends JpaRepository<Distribution, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Distribution d WHERE d.bloodRequest.id = :reqId")
    void deleteByRequestId(@Param("reqId") Long reqId);
}
