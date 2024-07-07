package com.lbadvisors.pffc.controllers.orders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.lbadvisors.pffc.validators.DynamicDateRange;
import com.lbadvisors.pffc.validators.NotOnSunday;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class OrderPostDto {
    private Integer customerId;

    @NotNull(message = "The delivery date cannot be null")
    @NotOnSunday(message = "The delivery date cannot be a Sunday")
    @DynamicDateRange(message = "Date must be within tomorrow and three months from today")
    private LocalDate deliveryDate;

    private Integer shipToId;

    @Positive(message = "The total price must be greater than zero")
    @Max(value = 10000, message = "The total prcie must be less than $10,000")
    private BigDecimal totalPrice;

    @NotEmpty
    @Valid
    private List<OrderProfilePostDto> orderProfiles;
}

@Value
class OrderProfilePostDto {
    private Integer profileDid;
    @Positive(message = "The quantity must be greater than zero")
    private BigDecimal quantity;
}
