package com.fintechnic.backend.repository;

import com.fintechnic.backend.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findBillsByUserId(Long userId);
}
