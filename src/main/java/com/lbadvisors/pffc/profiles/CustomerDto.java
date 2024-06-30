package com.lbadvisors.pffc.profiles;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerDto {

    @JsonProperty("id")
    int customerId;
    @JsonProperty("name")
    String customerName;

}
