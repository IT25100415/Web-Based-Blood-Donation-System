package com.bloodbank.service;

import com.bloodbank.model.Distribution;
import com.bloodbank.repository.DistributionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LogisticsService {

    @Autowired
    private DistributionRepository distributionRepository;

    public List<Distribution> getAllDistributions() {
        return distributionRepository.findAll();
    }

    public Distribution createDispatch(Distribution dist) {
        dist.setDispatchTime(LocalDateTime.now());
        dist.setStatus("IN_TRANSIT");
        return distributionRepository.save(dist);
    }

    public void updateStatus(Long id, String status, Double temp, String gps) {
        Optional<Distribution> opt = distributionRepository.findById(id);
        if (opt.isPresent()) {
            Distribution d = opt.get();
            if (status != null && !status.isEmpty()) d.setStatus(status);
            if (temp != null) d.setCurrentTemperature(temp);
            if (gps != null && !gps.isEmpty()) d.setGpsCoordinates(gps);
            distributionRepository.save(d);
        }
    }
    
    public void deleteDistribution(Long id) {
        distributionRepository.deleteById(id);
    }
}
