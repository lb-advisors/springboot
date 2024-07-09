package com.lbadvisors.pffc.controllers.delivery_stops;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class DeliveryStopGetDto {

    private int id;
    private String driverName;
    private int priority;
    private String customerPhone;
    private String deliveryAddress1;
    private String deliveryAddress2;
    private String deliveryAddress3;
    private LocalDateTime plannedArrivalTime;
    private LocalDateTime actualArrivalTime;
    private String fileUrl;

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}
