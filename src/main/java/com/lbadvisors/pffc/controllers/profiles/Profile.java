package com.lbadvisors.pffc.controllers.profiles;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "profile_child")
public class Profile {

    @Id
    private Integer profileDid;

    private String salesRepName;
    private String salesRepPhone;
    private int profileId;
    private Integer customerId;
    private String customerName;
    private Integer compItemId;
    private String profileDescription;
    private String profileInstruction;
    private String unitTypePd;
    private BigDecimal packSizePd;
    private BigDecimal salesPrice;
    private String salesRepEmail;
    private String customerContactName;
    private String customerCell;
    private String customerEmail;

    // @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, fetch =
    // FetchType.LAZY)
    @OneToMany(targetEntity = ShipTo.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    private List<ShipTo> shipTos;
}