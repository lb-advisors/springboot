package com.lbadvisors.pffc.controllers;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Routes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeid;

    private Long driverid;

    private String drivername;
    private Long customerid;
    private String customername;
    private String address;
    private boolean arrivalstatus;
    private Timestamp deliverydate;

}
