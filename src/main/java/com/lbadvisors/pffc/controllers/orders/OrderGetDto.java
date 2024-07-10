package com.lbadvisors.pffc.controllers.orders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class OrderGetDto {

    private Integer id;

    private Integer customerId;
    private String customerName;

    private String salesRepName;
    private String salesRepPhone;

    private LocalDate deliveryDate;
    private Integer shipToId;
    private String shipToName;

    private BigDecimal totalPrice;

    private List<OrderProfileGetDto> profiles;
}

@Data
@NoArgsConstructor
class OrderProfileGetDto {
    @JsonProperty("id")
    private Integer profileDid;
    private String profileDescription;
    private String unitType;
    private BigDecimal packSize;
    private BigDecimal price;
    private BigDecimal quantity;
}
