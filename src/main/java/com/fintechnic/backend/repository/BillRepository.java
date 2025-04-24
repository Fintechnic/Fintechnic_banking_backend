package com.fintechnic.backend.repository;

import com.fintechnic.backend.model.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Page<Bill> findByUserIdAndIsPaid(Long userId, Boolean isPaid, Pageable pageable);
    Optional<Bill> findByIdAndIsPaid(Long billId, Boolean isPaid);
}
