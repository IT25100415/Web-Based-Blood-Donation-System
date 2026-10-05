package com.bloodbank.repository;

import com.bloodbank.model.CampaignRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampaignRegistrationRepository extends JpaRepository<CampaignRegistration, Long> {
    List<CampaignRegistration> findByDonorIdOrderByRegistrationTimeDesc(Long donorId);
    boolean existsByCampaignIdAndDonorId(Long campaignId, Long donorId);
    int countByCampaignId(Long campaignId);
    List<CampaignRegistration> findByCampaignIdOrderByRegistrationTimeDesc(Long campaignId);
    int countBySlotId(Long slotId);
    List<CampaignRegistration> findByCampaignIdAndAttendanceStatus(Long campaignId, String status);
}
