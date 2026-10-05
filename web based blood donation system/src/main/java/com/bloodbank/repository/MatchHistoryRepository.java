package com.bloodbank.repository;

import com.bloodbank.model.MatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MatchHistoryRepository extends JpaRepository<MatchHistory, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM MatchHistory m WHERE m.bloodRequest.id = :reqId")
    void deleteByRequestId(@Param("reqId") Long reqId);
}
