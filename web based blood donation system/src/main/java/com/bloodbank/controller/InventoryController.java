package com.bloodbank.controller;

import com.bloodbank.model.StockBatch;
import com.bloodbank.service.BloodInventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private BloodInventoryService inventoryService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("title", "Blood Inventory");
        model.addAttribute("stockLevels", inventoryService.getAllStock());
        model.addAttribute("batches", inventoryService.getAllBatches());
        return "inventory";
    }

    @PostMapping("/add-batch")
    public String addBatch(@Valid @ModelAttribute StockBatch batch, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/inventory?error=validation";
        }
        if (batch.getExpirationDate().isBefore(batch.getCollectionDate()) || batch.getExpirationDate().isEqual(batch.getCollectionDate())) {
            return "redirect:/inventory?error=invalid_dates";
        }
        
        inventoryService.addBatch(batch);
        return "redirect:/inventory";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam String batchNumber, @RequestParam String status) {
        inventoryService.updateBatchStatus(batchNumber, status);
        return "redirect:/inventory";
    }

    @PostMapping("/delete")
    public String deleteBatch(@RequestParam String batchNumber) {
        inventoryService.deleteBatch(batchNumber);
        return "redirect:/inventory";
    }
}
