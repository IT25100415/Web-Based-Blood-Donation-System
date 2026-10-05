package com.bloodbank.repository;

import com.bloodbank.model.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {
    List<Campaign> findByStatusInOrderByStartTimeAsc(List<String> statuses);
    
    List<Campaign> findByOrganizerIdOrderByStartTimeDesc(Long organizerId);
    
    @Query("SELECT c FROM Campaign c WHERE c.organizer.id = :organizerId AND (:status IS NULL OR c.status = :status)")
    List<Campaign> findByOrganizerIdAndStatusFilter(@Param("organizerId") Long organizerId, @Param("status") String status);
}
