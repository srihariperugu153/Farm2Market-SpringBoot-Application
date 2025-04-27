package com.farmtomarket.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "Transaction")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;
    private int orderId;
    private double amount;
    @CreatedDate
    private LocalDate createdAt;
    private boolean isProduct;
}
