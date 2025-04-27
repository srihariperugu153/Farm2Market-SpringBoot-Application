package com.farmtomarket.application.service.serviceImpl;

import com.farmtomarket.application.config.UserContextHolder;
import com.farmtomarket.application.dto.PageResponse;
import com.farmtomarket.application.dto.TransactionDTO;
import com.farmtomarket.application.dto.UserDTO;
import com.farmtomarket.application.model.Role;
import com.farmtomarket.application.model.Transaction;
import com.farmtomarket.application.repository.EquipmentOrderRepository;
import com.farmtomarket.application.repository.ProductOrderRepository;
import com.farmtomarket.application.repository.TransactionRepository;
import com.farmtomarket.application.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ProductOrderRepository productOrderRepository;
    private final EquipmentOrderRepository equipmentOrderRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, ProductOrderRepository productOrderRepository, EquipmentOrderRepository equipmentOrderRepository) {
        this.transactionRepository = transactionRepository;
        this.productOrderRepository = productOrderRepository;
        this.equipmentOrderRepository = equipmentOrderRepository;
    }

    @Override
    public PageResponse<TransactionDTO> transactionsList(LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        UserDTO userDTO = UserContextHolder.getUserDto();
        boolean accessProduct;
        accessProduct = !userDTO.getRoleName().equals(Role.FARMER.toString());
        Page<Transaction> transactions = transactionRepository.findByCreatedAtBetweenAndIsProduct(fromDate,toDate,accessProduct,pageable);
        PageResponse<TransactionDTO> pageResponse = new PageResponse<>();
        List<TransactionDTO> transactionDTOList = transactions.getContent().stream().map(transaction -> {
            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setId(transaction.getTransactionId());
            transactionDTO.setAmount(transaction.getAmount());
            transactionDTO.setOrderId(transaction.getOrderId());
            if (transaction.isProduct()){
                transactionDTO.setPurchasedItemName(productOrderRepository.findById(transaction.getOrderId())
                        .orElseThrow().getProduct().getProductName());
            } else {
                transactionDTO.setPurchasedItemName(equipmentOrderRepository.findById(transaction.getOrderId())
                        .orElseThrow().getEquipment().getEquipName());
            }
            transactionDTO.setPurchasedDate(String.valueOf(transaction.getCreatedAt()));
            return transactionDTO;
        }).toList();
        pageResponse.setContent(transactionDTOList);
        pageResponse.setPageNumber(transactions.getNumber());
        pageResponse.setPageSize(transactions.getSize());
        pageResponse.setTotalElements(transactions.getTotalElements());
        pageResponse.setTotalPages(transactions.getTotalPages());
        return pageResponse;
    }
}
