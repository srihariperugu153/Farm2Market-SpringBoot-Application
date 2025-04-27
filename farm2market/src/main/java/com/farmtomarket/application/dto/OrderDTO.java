package com.farmtomarket.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sun.jdi.event.StepEvent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {

    private int id;
    private String equipmentName;
    private String productName;
    private int orderQuantity;
    private String fromDate;
    private String toDate;
    private LocalDate updatedAt;
    private int dueDays;
    private boolean returnStatus;

    public void setToDate(LocalDate toDate){
        this.toDate = String.valueOf(toDate);
    }

    public void setFromDate(LocalDate fromDate){
        this.fromDate = String.valueOf(fromDate);
    }
}
