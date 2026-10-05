package com.bloodbank.service;

import com.bloodbank.model.Campaign;
import com.bloodbank.model.CampaignSlot;
import com.bloodbank.repository.CampaignRepository;
import com.bloodbank.repository.CampaignSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

@Service
public class CampaignService {

    @Autowired
    private CampaignRepository campaignRepository;
    
    @Autowired
    private CampaignSlotRepository slotRepository;

    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }
    
    public List<Campaign> getCampaignsByOrganizer(Long organizerId) {
        return campaignRepository.findByOrganizerIdOrderByStartTimeDesc(organizerId);
    }
    
    public Campaign getCampaignById(Long id) {
        return campaignRepository.findById(id).orElse(null);
    }

    @Transactional
    public Campaign createCampaign(Campaign campaign, List<CampaignSlot> slots) {
        if (campaign.getStatus() == null || campaign.getStatus().isEmpty()) {
            campaign.setStatus("DRAFT");
        }
        
        // Generate Campaign Ref CMP-YYYYMMDD-XXXX
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randPart = String.format("%04d", (int)(Math.random() * 10000));
        campaign.setCampaignRef("CMP-" + datePart + "-" + randPart);
        
        Campaign saved = campaignRepository.save(campaign);
        
        if (slots != null) {
            for (CampaignSlot slot : slots) {
                slot.setCampaign(saved);
                slotRepository.save(slot);
            }
        }
        
        return saved;
    }

    @Transactional
    public void updateCampaign(Campaign updatedCampaign, List<CampaignSlot> slots) {
        Optional<Campaign> opt = campaignRepository.findById(updatedCampaign.getId());
        if (opt.isPresent()) {
            Campaign c = opt.get();
            c.setCampaignName(updatedCampaign.getCampaignName());
            c.setDescription(updatedCampaign.getDescription());
            c.setVenue(updatedCampaign.getVenue());
            c.setCity(updatedCampaign.getCity());
            c.setFullAddress(updatedCampaign.getFullAddress());
            c.setTargetCapacity(updatedCampaign.getTargetCapacity());
            c.setTargetDonationUnits(updatedCampaign.getTargetDonationUnits());
            c.setStartTime(updatedCampaign.getStartTime());
            c.setEndTime(updatedCampaign.getEndTime());
            c.setStatus(updatedCampaign.getStatus());
            
            campaignRepository.save(c);
            
            // For simplicity, we wipe existing slots and recreate them if provided
            if (slots != null && !slots.isEmpty()) {
                slotRepository.deleteByCampaignId(c.getId());
                for (CampaignSlot slot : slots) {
                    slot.setCampaign(c);
                    slotRepository.save(slot);
                }
            }
        }
    }

    public void updateCampaignStatus(Long id, String status) {
        Optional<Campaign> opt = campaignRepository.findById(id);
        if (opt.isPresent()) {
            Campaign c = opt.get();
            c.setStatus(status);
            campaignRepository.save(c);
        }
    }
    
    public void cancelCampaign(Long id, String reason) {
        Optional<Campaign> opt = campaignRepository.findById(id);
        if (opt.isPresent()) {
            Campaign c = opt.get();
            c.setStatus("CANCELLED");
            c.setCancellationReason(reason);
            campaignRepository.save(c);
        }
    }

    public void deleteCampaign(Long id) {
        campaignRepository.deleteById(id);
    }
}
