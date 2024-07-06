package com.lbadvisors.pffc.orders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class OrderPostDto {
    private Integer customerId;

    @Future(message = "The delivery date needs to be after today")
    private LocalDate deliveryDate;

    private Integer shipToId;

    @Positive(message = "The total price must be greater than zero")
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
