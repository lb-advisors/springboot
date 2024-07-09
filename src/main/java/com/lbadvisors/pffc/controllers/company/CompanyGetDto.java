package com.lbadvisors.pffc.controllers.company;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class CompanyGetDto {

    @JsonProperty("id")
    Integer companyId;

    @JsonProperty("name")
    String companyName;

}
