package com.lbadvisors.pffc.profiles;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
public class ProfileGetDto {

    Integer customerId;
    private String customerEmail;
    private String customerName;

    private String salesRepName;
    private String salesRepPhone;

    // private String profileDid;
    private List<ProfileDto> profiles;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile")
    private List<ShipToGetDto> shipTos;
}
