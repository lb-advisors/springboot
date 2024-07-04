package com.lbadvisors.pffc.delivery_stops;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DeliveryStopGetDto {
    private int id;
    private String driverName;
    private int priority;
    private String deliveryAddress1;
    private String deliveryAddress2;
    private String deliveryAddress3;
    private LocalDateTime plannedArrivalTime;
    private LocalDateTime actualArrivalTime;
}
