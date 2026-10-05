package com.bloodbank.repository;

import com.bloodbank.model.BloodRequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface BloodRequestHistoryRepository extends JpaRepository<BloodRequestHistory, Long> {
    List<BloodRequestHistory> findByBloodRequestIdOrderByTimestampDesc(Long bloodRequestId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM BloodRequestHistory h WHERE h.bloodRequest.id = :reqId")
    void deleteByBloodRequestId(@Param("reqId") Long reqId);
}
