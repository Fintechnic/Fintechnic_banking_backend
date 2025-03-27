package com.fintechnic.backend.controller;

import com.fintechnic.backend.model.Bill;
import com.fintechnic.backend.service.BillService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BillController {
    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping("/bill")
    public ResponseEntity<List<Bill>> getBills(@RequestParam Long userId) {
        List<Bill> bills = billService.getBillsList(userId);

        if (bills == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().body(bills);
    }

    @PostMapping("/api/admin/newbill")
    public ResponseEntity<?> createBill(@RequestBody Bill bill) {
        try {
            Bill newBill = billService.createBill(bill);
            return ResponseEntity.ok().body(newBill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
