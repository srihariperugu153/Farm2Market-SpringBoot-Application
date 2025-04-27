package com.farmtomarket.application.service;

import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.TransactionDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface TransactionService {
    PageResponse<TransactionDTO> transactionsList(LocalDate fromDate, LocalDate toDate, Pageable pageable);
}
