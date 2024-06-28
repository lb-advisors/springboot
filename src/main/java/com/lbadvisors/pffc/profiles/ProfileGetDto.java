package com.lbadvisors.pffc.profiles;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ProfileGetDto {

    @JsonProperty("id")
    int profileDid;

    int customerId;
    private String customerEmail;
    private String customerName;

    private String salesRepName;
    private String salesRepCell;

    // private String profileDid;
    private String profileDescription;
    private String unitTypePd;
    BigDecimal packSizePd;
    BigDecimal salesPrice;

}
