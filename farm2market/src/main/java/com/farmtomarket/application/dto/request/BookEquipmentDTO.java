package com.farmtomarket.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BookEquipmentDTO {
    @Schema(description = "ID of the equipment", example = "1")
    @NotNull(message = "Equipment Name Required")
    private int equipmentId;

    @Schema(description = "Start date of the rental period", example = "2025-02-01")
    @NotNull(message = "Rental Period Date is Required")
    @FutureOrPresent(message = "Don't Enter Past Days")
    private LocalDate fromDate;

    @Schema(description = "End date of the rental period", example = "2025-02-10")
    @NotNull(message = "Rental Period Date is Required")
    @FutureOrPresent(message = "Don't Enter Past Days")
    private LocalDate toDate;

    @Schema(description = "Quantity of the equipment to be rented", example = "1")
    @NotNull(message = "Quantity Required")
    @Min(value = 1,message = "Quantity should be minimum 1")
    private int quantity;

    @Schema(description = "Payment received for the rental", example = "1500.00")
    @NotNull(message = "Enter Payment")
    private double receivedPayment;
}
