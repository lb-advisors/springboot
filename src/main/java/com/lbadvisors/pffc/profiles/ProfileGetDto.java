package com.lbadvisors.pffc.profiles;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.Value;

@Data
public class ProfileGetDto {

    @JsonProperty("id")
    Integer profileDid;

    Integer customerId;
    private String customerEmail;
    private String customerName;

    private String salesRepName;
    private String salesRepPhone;

    // private String profileDid;
    private String profileDescription;
    private String unitTypePd;
    BigDecimal packSizePd;
    BigDecimal salesPrice;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile")
    private List<ShipToGetDto> shipTos;
}
