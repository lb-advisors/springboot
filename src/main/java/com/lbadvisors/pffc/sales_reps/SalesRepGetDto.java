package com.lbadvisors.pffc.sales_reps;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class SalesRepGetDto {

    @JsonProperty("name")
    String salesRepName;

}
