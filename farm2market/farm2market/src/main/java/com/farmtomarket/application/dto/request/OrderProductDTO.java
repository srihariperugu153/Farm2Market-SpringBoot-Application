package com.farmtomarket.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductDTO {
    @NotNull(message = "Product name Required")
    private int prodId;
    @NotNull(message = "Quantity Required")
    private int quantity;
    @NotNull(message = "Enter Amount")
    private double amount;
}
