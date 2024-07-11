package com.lbadvisors.pffc.controllers.profiles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ShipTo {

    @Id
    @Column(name = "ship_to_id")
    private Integer id;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    // private Profile profile;

    // @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    // @Column(name = "customer_id")
    private Integer customerId;

    private String shipToName;
    private String shipToAddress;
}
