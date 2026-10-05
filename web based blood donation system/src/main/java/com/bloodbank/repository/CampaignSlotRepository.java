package com.bloodbank.repository;

import com.bloodbank.model.CampaignSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaignSlotRepository extends JpaRepository<CampaignSlot, Long> {
    List<CampaignSlot> findByCampaignIdOrderByStartTimeAsc(Long campaignId);
    void deleteByCampaignId(Long campaignId);
}
