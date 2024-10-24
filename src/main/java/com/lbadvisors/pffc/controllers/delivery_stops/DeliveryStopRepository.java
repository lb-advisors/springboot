package com.lbadvisors.pffc.controllers.delivery_stops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lbadvisors.pffc.controllers.drivers.DriverGetDto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeliveryStopRepository extends JpaRepository<DeliveryStop, Integer> {

    List<DeliveryStop> findByDriverNameAndDeliveryDateOrderByPriorityAsc(String driverName, LocalDate deliveryDate);

    @Query("SELECT DISTINCT NEW com.lbadvisors.pffc.controllers.drivers.DriverGetDto(c.driverName) FROM DeliveryStop c ORDER BY c.driverName")
    List<DriverGetDto> findDistinctDriverNames();

}
