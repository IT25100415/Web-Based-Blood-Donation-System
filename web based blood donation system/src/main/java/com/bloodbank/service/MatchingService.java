package com.bloodbank.service;

import com.bloodbank.model.Donor;
import com.bloodbank.model.MatchHistory;
import com.bloodbank.model.BloodRequest;
import com.bloodbank.repository.DonorRepository;
import com.bloodbank.repository.MatchHistoryRepository;
import com.bloodbank.repository.BloodRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private DonorRepository donorRepository;
    
    @Autowired
    private MatchHistoryRepository matchRepository;
    
    @Autowired
    private BloodRequestRepository requestRepository;

    public List<MatchHistory> getAllMatches() {
        return matchRepository.findAll();
    }
    
    public List<MatchHistory> getMatchesByHospitalId(Long hospitalId) {
        return matchRepository.findAll().stream()
                .filter(m -> m.getBloodRequest() != null 
                          && m.getBloodRequest().getHospital() != null 
                          && m.getBloodRequest().getHospital().getId().equals(hospitalId))
                .collect(Collectors.toList());
    }
    
    public List<MatchHistory> getMatchesByDonorId(Long donorId) {
        return matchRepository.findAll().stream()
                .filter(m -> m.getDonor() != null && m.getDonor().getId().equals(donorId))
                .collect(Collectors.toList());
    }

    public List<Donor> findMatchingDonors(String bloodGroup, String city) {
        return donorRepository.findAll().stream()
                .filter(d -> d.getBloodGroup() != null && d.getBloodGroup().equals(bloodGroup))
                .filter(d -> city == null || city.isEmpty() || (d.getCity() != null && d.getCity().equalsIgnoreCase(city)))
                .collect(Collectors.toList());
    }

    public MatchHistory notifyDonor(Long requestId, Long donorId) {
        BloodRequest request = requestRepository.findById(requestId).orElse(null);
        Donor donor = donorRepository.findById(donorId).orElse(null);
        
        if (request != null && donor != null) {
            MatchHistory match = new MatchHistory();
            match.setBloodRequest(request);
            match.setDonor(donor);
            match.setMatchTime(LocalDateTime.now());
            match.setStatus("NOTIFIED");
            return matchRepository.save(match);
        }
        return null;
    }
    
    public MatchHistory updateStatus(Long id, String status) {
        Optional<MatchHistory> opt = matchRepository.findById(id);
        if (opt.isPresent()) {
            MatchHistory match = opt.get();
            match.setStatus(status);
            return matchRepository.save(match);
        }
        return null;
    }
    
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }
}
