package com.lbadvisors.pffc.delivery_stops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryStopRepository extends JpaRepository<DeliveryStop, Integer> {

    List<DeliveryStop> findByDriverNameAndDeliveryDate(String driverName, LocalDate deliveryDate);

}
