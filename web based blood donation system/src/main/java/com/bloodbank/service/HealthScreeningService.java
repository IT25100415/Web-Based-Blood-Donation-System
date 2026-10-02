package com.bloodbank.service;

import com.bloodbank.model.HealthScreening;
import com.bloodbank.model.Donor;
import com.bloodbank.repository.HealthScreeningRepository;
import com.bloodbank.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HealthScreeningService {

    @Autowired
    private HealthScreeningRepository repository;
    
    @Autowired
    private DonorRepository donorRepository;

    public HealthScreening requestScreening(HealthScreening screening) {
        screening.setRequestDate(LocalDateTime.now());
        screening.setStatus(HealthScreening.ScreeningStatus.PENDING);
        return repository.save(screening);
    }

    public List<HealthScreening> getAllScreenings() {
        return repository.findAll();
    }
    
    public List<HealthScreening> getScreeningsByDonorId(Long donorId) {
        return repository.findAll().stream()
                .filter(s -> s.getDonor() != null && s.getDonor().getId().equals(donorId))
                .toList();
    }

    public HealthScreening getScreeningById(Long id) {
        return repository.findById(id).orElse(null);
    }
    
    public HealthScreening updateScreeningTests(Long id, String tests) {
        Optional<HealthScreening> opt = repository.findById(id);
        if (opt.isPresent()) {
            HealthScreening screening = opt.get();
            screening.setRequestedTests(tests);
            return repository.save(screening);
        }
        return null;
    }

    public HealthScreening approveAndSchedule(Long id, LocalDateTime scheduledDate) {
        Optional<HealthScreening> opt = repository.findById(id);
        if (opt.isPresent()) {
            HealthScreening screening = opt.get();
            screening.setStatus(HealthScreening.ScreeningStatus.APPROVED);
            screening.setScheduledDate(scheduledDate);
            return repository.save(screening);
        }
        return null;
    }

    public HealthScreening updateResults(Long id, String hepB, String hepC, String hiv, String dengue, String bloodGroup) {
        Optional<HealthScreening> opt = repository.findById(id);
        if (opt.isPresent()) {
            HealthScreening screening = opt.get();
            screening.setHepB(hepB);
            screening.setHepC(hepC);
            screening.setHiv(hiv);
            screening.setDengue(dengue);
            screening.setBloodGroupConfirmed(bloodGroup);
            screening.setStatus(HealthScreening.ScreeningStatus.COMPLETED);
            
            // Business Logic: Update Donor based on lab results!
            Donor donor = screening.getDonor();
            if (donor != null) {
                // Update verified blood group if provided
                if (bloodGroup != null && !bloodGroup.isEmpty()) {
                    donor.setBloodGroup(bloodGroup);
                }
                
                // Suspend donor if infectious diseases are positive
                if ("POSITIVE".equalsIgnoreCase(hepB) || 
                    "POSITIVE".equalsIgnoreCase(hepC) || 
                    "POSITIVE".equalsIgnoreCase(hiv)) {
                    donor.setIsActive(false); // Mark as ineligible to donate
                } else {
                    donor.setIsActive(true); // Ensure they remain active if tests pass
                }
                donorRepository.save(donor);
            }
            
            return repository.save(screening);
        }
        return null;
    }

    public void cancelScreening(Long id) {
        Optional<HealthScreening> opt = repository.findById(id);
        if (opt.isPresent()) {
            HealthScreening screening = opt.get();
            screening.setStatus(HealthScreening.ScreeningStatus.CANCELLED);
            repository.save(screening);
        }
    }
    
    public void deleteScreening(Long id) {
        repository.deleteById(id);
    }
}
