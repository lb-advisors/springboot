package com.lbadvisors.pffc.delivery_stops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryStopRepository extends JpaRepository<DeliveryStop, Integer> {

    List<DeliveryStop> findByDriverNameAndDeliveryDateOrderByPriorityAsc(String driverName, LocalDate deliveryDate);

    @Query("SELECT DISTINCT c.driverName FROM DeliveryStop c")
    List<String> findDistinctDriverNames();

}
