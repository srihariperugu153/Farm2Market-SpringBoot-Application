package com.farmtomarket.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentDTO {

    private int equipId;
    @NotNull(message = "Equipment name Required")
    private String equipName;
    @NotNull(message = "Quantity Required")
    private double quantity;
    @NotNull(message = "Rent Cost Required")
    private double rentCost;
    @NotNull(message = "Pincode Required")
    private long pinCode;
    @NotNull(message = "City Required")
    private String city;
    @NotNull(message = "State Required")
    private String state;
}
