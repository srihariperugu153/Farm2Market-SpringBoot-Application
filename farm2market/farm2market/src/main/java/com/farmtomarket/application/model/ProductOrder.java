package com.farmtomarket.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "Product_Order")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class ProductOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;
    @ManyToOne
    private User trader;
    @ManyToOne
    private Products product;
    private double productQuantity;
    @CreatedDate
    private LocalDate createdAt;
}
