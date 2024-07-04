package com.lbadvisors.pffc.profiles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @JoinColumn(name = "customer_id", referencedColumnName = "customerId")
    @Column(name = "customer_id")
    private String customerId;

    private String shipToName;
    private String shipToAddress;
}
