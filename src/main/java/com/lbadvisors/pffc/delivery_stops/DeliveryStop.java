package com.lbadvisors.pffc.delivery_stops;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class DeliveryStop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    private String driverName;
    private int invoiceNumber;
    private LocalDate deliveryDate;
    private int priority;
    @Column(name = "delivery_address_1")
    private String deliveryAddress1;
    @Column(name = "delivery_address_2")
    private String deliveryAddress2;
    @Column(name = "delivery_address_3")
    private String deliveryAddress3;
    private String customerPhone;
    private LocalDateTime plannedArrivalTime;
    private LocalDateTime actualArrivalTime;
    private String comments;
    private String rating;

    @Column(name = "s3_file_key")
    private String s3FileKey;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    private String lastUpdatedBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt;
}
