package com.lbadvisors.pffc.controllers.orders;

import java.math.BigDecimal;
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
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Data
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
@Table(name = "`order`", uniqueConstraints = { @UniqueConstraint(columnNames = { "customerId", "deliveryDate", "shipToId" }) })

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    private Integer orderId;

    private Integer customerId;
    private String customerName;
    private String customerEmail;

    private String salesRepName;
    private String salesRepPhone;

    private Integer profileDid;
    private String profileDescription;
    private String unitType;
    private BigDecimal packSize;

    private BigDecimal price;

    private BigDecimal quantity;

    private String customerPo;
    private BigDecimal totalPrice;

    private LocalDate deliveryDate;

    private Integer profileId;

    private Integer shipToId;
    private String shipToName;

    private Integer companyId;

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
