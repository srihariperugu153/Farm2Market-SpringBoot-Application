package com.farmtomarket.application.repository;

import com.farmtomarket.application.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    Page<Transaction> findByCreatedAtBetweenAndIsProduct(LocalDate fromDate, LocalDate toDate, boolean accessProduct, Pageable pageable);
}
