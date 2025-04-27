package com.farmtomarket.application.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Table(name = "Equipments")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Equipments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int equipId;
    private String equipName;
    private double quantity;
    private double rentCost;
    private long pinCode;
    private boolean deleteFlag;
    private String city;
    private String state;
    @CreatedDate
    private LocalDate createdAt;
    @ManyToOne
    private User user;
}
