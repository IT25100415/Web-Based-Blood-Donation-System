package com.bloodbank.service;

import com.bloodbank.model.BloodStock;
import com.bloodbank.model.StockBatch;
import com.bloodbank.repository.BloodStockRepository;
import com.bloodbank.repository.StockBatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BloodInventoryService {

    @Autowired
    private BloodStockRepository stockRepository;
    
    @Autowired
    private StockBatchRepository batchRepository;

    public List<BloodStock> getAllStock() {
        return stockRepository.findAll();
    }

    public List<StockBatch> getAllBatches() {
        return batchRepository.findAll();
    }

    public StockBatch addBatch(StockBatch batch) {
        batch.setStatus("AVAILABLE");
        StockBatch saved = batchRepository.save(batch);
        updateStockCount(batch.getBloodGroup());
        return saved;
    }
    
    public void updateBatchStatus(String batchNumber, String status) {
        Optional<StockBatch> opt = batchRepository.findById(batchNumber);
        if (opt.isPresent()) {
            StockBatch batch = opt.get();
            batch.setStatus(status);
            batchRepository.save(batch);
            updateStockCount(batch.getBloodGroup());
        }
    }
    
    public void deleteBatch(String batchNumber) {
        Optional<StockBatch> opt = batchRepository.findById(batchNumber);
        if (opt.isPresent()) {
            String bg = opt.get().getBloodGroup();
            batchRepository.deleteById(batchNumber);
            updateStockCount(bg);
        }
    }

    private void updateStockCount(String bloodGroup) {
        // Count all AVAILABLE batches for this blood group
        long count = batchRepository.findAll().stream()
                .filter(b -> b.getBloodGroup().equals(bloodGroup) && "AVAILABLE".equals(b.getStatus()))
                .mapToInt(b -> b.getUnits() != null ? b.getUnits() : 1)
                .sum();
                
        BloodStock stock = stockRepository.findById(bloodGroup).orElse(new BloodStock());
        stock.setBloodGroup(bloodGroup);
        stock.setTotalUnits((int) count);
        stockRepository.save(stock);
    }
}

