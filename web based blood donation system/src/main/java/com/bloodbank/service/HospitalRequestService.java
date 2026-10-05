package com.bloodbank.service;

import com.bloodbank.model.BloodRequest;
import com.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.repository.BloodRequestHistoryRepository;
import com.bloodbank.repository.MatchHistoryRepository;
import com.bloodbank.repository.DistributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HospitalRequestService {

    @Autowired
    private BloodRequestRepository requestRepository;
    
    @Autowired
    private BloodRequestHistoryRepository historyRepository;
    
    @Autowired
    private MatchHistoryRepository matchRepository;
    
    @Autowired
    private DistributionRepository distRepository;

    public List<BloodRequest> getAllRequests() {
        return requestRepository.findAll();
    }
    
    public List<BloodRequest> getRequestsByHospitalId(Long hospitalId) {
        return requestRepository.findAll().stream()
                .filter(r -> r.getHospital() != null && r.getHospital().getId().equals(hospitalId))
                .collect(Collectors.toList());
    }

    public BloodRequest submitRequest(BloodRequest request) {
        request.setRequestedDate(LocalDateTime.now());
        request.setStatus(BloodRequest.Status.PENDING);
        return requestRepository.save(request);
    }

    public BloodRequest updateRequest(Long id, BloodRequest updateData) {
        Optional<BloodRequest> opt = requestRepository.findById(id);
        if (opt.isPresent()) {
            BloodRequest request = opt.get();
            // Only update allowed fields
            request.setBloodGroup(updateData.getBloodGroup());
            request.setUnitsRequired(updateData.getUnitsRequired());
            request.setUrgencyLevel(updateData.getUrgencyLevel());
            if (updateData.getNeededDate() != null) {
                request.setNeededDate(updateData.getNeededDate());
            }
            return requestRepository.save(request);
        }
        return null;
    }

    public BloodRequest updateRequestStatus(Long id, BloodRequest.Status status) {
        Optional<BloodRequest> opt = requestRepository.findById(id);
        if (opt.isPresent()) {
            BloodRequest request = opt.get();
            request.setStatus(status);
            return requestRepository.save(request);
        }
        return null;
    }

    @Transactional
    public void deleteRequest(Long id) {
        historyRepository.deleteByBloodRequestId(id);
        matchRepository.deleteByRequestId(id);
        distRepository.deleteByRequestId(id);
        requestRepository.deleteById(id);
    }
}
