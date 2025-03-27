package com.fintechnic.backend.service;

import com.fintechnic.backend.model.Bill;
import com.fintechnic.backend.model.User;
import com.fintechnic.backend.repository.BillRepository;
import com.fintechnic.backend.repository.UserRepository;
import com.fintechnic.backend.util.CryptoUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BillService {
    private final BillRepository billRepository;
    private final UserRepository userRepository;

    public BillService(BillRepository billRepository, UserRepository userRepository) {
        this.billRepository = billRepository;
        this.userRepository = userRepository;
    }

    public List<Bill> getBillsList(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return null;
        }
        
        return billRepository.findBillsByUserId(userId);
    }

    public Bill createBill(Bill bill) {
        return billRepository.save(bill);
    }
}
