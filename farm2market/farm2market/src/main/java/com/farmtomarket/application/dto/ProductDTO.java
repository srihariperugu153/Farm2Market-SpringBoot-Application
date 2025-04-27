package com.farmtomarket.application.dto;

import com.farmtomarket.application.model.Products;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private int id;
    @NotNull(message = "Product name required")
    private String productName;
    @NotNull(message = "Quantity Required")
    private double quantity;
    @NotNull(message = "Price Required")
    private double price;
    @NotNull(message = "City Required")
    private String city;

    public ProductDTO convertToProduct(Products products){
        this.id = products.getProductId();
        this.productName = products.getProductName();
        this.quantity = products.getQuantity();
        this.price = products.getPrice();
        this.city = products.getCity();
        return this;
    }

}
