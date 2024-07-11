package com.lbadvisors.pffc.controllers.inventory;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comp_item_id")
    Integer id;

    private String compDescription;
    private String packSize;
    private String unitType;
    private BigDecimal activePrice;
    private String woh;
}
