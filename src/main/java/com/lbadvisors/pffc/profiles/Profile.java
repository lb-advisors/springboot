package com.lbadvisors.pffc.profiles;

import java.math.BigDecimal;

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
    private int profileDid;

    private String salesRepName;
    private String salesRepCell;
    private int profileId;
    private int customerId;
    private String customerName;
    private int compItemId;
    private String profileDescription;
    private String profileInstruction;
    private String unitTypePd;
    private BigDecimal packSizePd;
    private BigDecimal salesPrice;
    private String salesRepEmail;
    private String customerContactName;
    private String customerCell;
    private String customerEmail;
}
