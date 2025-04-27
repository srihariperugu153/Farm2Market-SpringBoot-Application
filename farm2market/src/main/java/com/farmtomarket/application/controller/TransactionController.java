package com.farmtomarket.application.controller;

import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.TransactionDTO;
import com.farmtomarket.application.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/txc")
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<PageResponse<TransactionDTO>> getTransactions(@RequestParam(required = false,defaultValue = "0") int pageNo,
                                                                        @RequestParam(required = false,defaultValue = "10") int pageSize,
                                                                        @RequestParam LocalDate fromDate,
                                                                        @RequestParam LocalDate toDate){
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        return ResponseEntity.ok(transactionService.transactionsList(fromDate,toDate,pageable));
    }

}
