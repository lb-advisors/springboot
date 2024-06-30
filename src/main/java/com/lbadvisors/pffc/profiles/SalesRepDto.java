package com.lbadvisors.pffc.profiles;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesRepDto {

    @JsonProperty("name")
    String salesRepName;

}
