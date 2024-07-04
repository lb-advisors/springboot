package com.lbadvisors.pffc.orders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

@Value
public class OrderPostDto {
    private Integer customerId;
    private LocalDate deliveryDate;
    private Integer shipToId;
    private BigDecimal totalPrice;
    @NotEmpty
    private List<OrderProfilePostDto> orderProfiles;
}

@Value
class OrderProfilePostDto {
    private Integer profileDid;
    private BigDecimal quantity;
}
