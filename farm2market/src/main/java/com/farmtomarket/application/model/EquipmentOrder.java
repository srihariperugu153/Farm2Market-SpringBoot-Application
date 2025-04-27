package com.farmtomarket.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name="Equipment_Order")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class EquipmentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @ManyToOne
    private User equipmentOrderedFarmer;

    @ManyToOne
    private Equipments equipment;

    private int equipmentQuantity;
    private LocalDate toDate;
    @CreatedDate
    private LocalDate createdAt;
    private LocalDate fromDate;
    @LastModifiedDate
    private LocalDate updatedAt;
    private int dueDays;
    private boolean returnStatus;
}
