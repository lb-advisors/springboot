package com.lbadvisors.pffc.controllers.drivers;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class DriverGetDto {

    @JsonProperty("name")
    String driverName;

}
