package com.lbadvisors.pffc.profiles;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@DynamicUpdate
@Table(name = "profile_child")
public class Profile {

    @Id
    @Column(name = "profile_did")
    int profileDid;

    private String salesRepName;
    private String salesRepCell;
    int profileId;
    int customerId;
    int compItemId;
    private String profileDescription;
    private String profileInstruction;
    private String unitTypePd;
    float packSizePd;
    float salesPrice;
    private String salesRepEmail;
    private String customerContactName;
    private String customerCell;
    private String customerEmail;
}
