package com.lbadvisors.pffc.delivery_stops;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lbadvisors.pffc.exception.ResourceNotFoundException;

@RestController
@CrossOrigin
@RequestMapping("/delivery-stops")
public class DeliveryStopController {

    @Autowired
    private DeliveryStopService deliveryStopsService;

    @GetMapping(value = "")
    public ResponseEntity<List<DeliveryStopGetDto>> getAllDeliveryStops(@RequestParam String driverName,
            @RequestParam LocalDate deliveryDate) {

        return new ResponseEntity<>(
                deliveryStopsService.findByDriverNameAndDeliveryDate(driverName, deliveryDate), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DeliveryStopGetDto> updateArrivalTime(@PathVariable("id") int id) {

        DeliveryStopGetDto deliveryStopGetDto = deliveryStopsService.updateArrivalTime(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource nor found"));
        return ResponseEntity.ok(deliveryStopGetDto);
    }

    /*
     * @GetMapping(value = "/{id}")
     * public ResponseEntity<DeliveryStops> getRouteById(@PathVariable("id") int id)
     * throws Exception {
     * 
     * Routes route = routesService.getRoute(id)
     * .orElseThrow(() -> new
     * ResourceNotFoundException(String.format("Route with ID %s not found", id)));
     * return new ResponseEntity<>(route, HttpStatus.OK);
     * // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
     * }
     */
}
