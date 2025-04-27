package com.farmtomarket.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentRequestDTO {
    @NotNull(message = "Equipment name required")
    private String equipName;
    @NotNull(message = "Quantity required")
    private double quantity;
    @NotNull(message = "Rent Cost required")
    private double rentCost;
    @NotNull(message = "Pincode required")
    private long pinCode;
    @NotNull(message = "City required")
    private String city;
    @NotNull(message = "State required")
    private String state;
}
