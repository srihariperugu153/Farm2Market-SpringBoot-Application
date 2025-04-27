package com.farmtomarket.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDTO {
    private int id;
    private int orderId;
    private String purchasedItemName;
    private double amount;
    private String purchasedDate;
}
