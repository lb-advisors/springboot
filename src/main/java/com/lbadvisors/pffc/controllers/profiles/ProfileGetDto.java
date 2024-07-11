package com.lbadvisors.pffc.controllers.profiles;

import java.util.List;
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

    // @OneToMany(cascade = CascadeType.ALL, mappedBy = "profile")
    private List<ShipToGetDto> shipTos;
}
