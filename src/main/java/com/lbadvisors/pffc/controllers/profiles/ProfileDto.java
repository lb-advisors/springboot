package com.lbadvisors.pffc.controllers.profiles;

import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProfileDto {
    // private String profileDid;
    @JsonProperty("id")
    Integer profileDid;

    private String profileDescription;
    private String unitTypePd;
    BigDecimal packSizePd;
    BigDecimal salesPrice;

}