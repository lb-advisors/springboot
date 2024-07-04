package com.lbadvisors.pffc.customers;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CustomerGetDto {

    @JsonProperty("id")
    int customerId;
    @JsonProperty("name")
    String customerName;

}
