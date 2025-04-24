package com.fintechnic.backend.controller;

import com.fintechnic.backend.dto.BillRequestDTO;
import com.fintechnic.backend.dto.BillResponseDTO;
import com.fintechnic.backend.dto.TransactionDTO;
import com.fintechnic.backend.model.Bill;
import com.fintechnic.backend.service.BillService;
import com.fintechnic.backend.util.JwtUtil;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BillController {
    private final BillService billService;
    private final JwtUtil jwtUtil;

    public BillController(BillService billService, JwtUtil jwtUtil) {
        this.billService = billService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/bills")
    public ResponseEntity<Page<BillResponseDTO>> getUserBills(@RequestHeader("Authorization") String authHeader,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {

        try {
            Long userId = jwtUtil.extractUserIdFromToken(authHeader);
            Page<BillResponseDTO> bills = billService.getBillsByUserId(userId, page, size);
            return ResponseEntity.ok(bills);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/bills/{billId}/pay")
    public ResponseEntity<TransactionDTO> payBill(@PathVariable Long billId,
                                                  @RequestHeader("Authorization") String authHeader) {

        try {
            Long userId = jwtUtil.extractUserIdFromToken(authHeader);
            TransactionDTO payment = billService.payBill(billId, userId);
            return ResponseEntity.ok(payment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/admin/new-bill")
    public ResponseEntity<BillResponseDTO> createBill(@RequestBody BillRequestDTO request) {
        try {
            BillResponseDTO response = billService.createBill(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
