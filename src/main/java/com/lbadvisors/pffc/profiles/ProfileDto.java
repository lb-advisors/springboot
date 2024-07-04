package com.lbadvisors.pffc.profiles;

import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
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
