package com.lbadvisors.pffc.profiles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping(value = "/customers/{id}/profiles")
    public ResponseEntity<List<ProfileGetDto>> getProfiles(@PathVariable("id") int customerId) {

        return new ResponseEntity<List<ProfileGetDto>>(
                profileService.findByCustomerId(customerId), HttpStatus.OK);
    }

    @GetMapping(value = "/sales-reps")
    public ResponseEntity<List<String>> getSalesRep() {
        return new ResponseEntity<List<String>>(
                profileService.getAllDriverName(), HttpStatus.OK);
    }

    @GetMapping(value = "/sales-reps/{id}/customers")
    public ResponseEntity<List<String>> getAllCustomers(@PathVariable("id") String salesRepName) {

        return new ResponseEntity<List<String>>(
                profileService.getAllCustomers(salesRepName), HttpStatus.OK);
    }

    /*
     * @PostMapping(value = "/customers/{id}/orders")
     * public ResponseEntity<Order> createOrder(@RequestBody OrderPostDTO
     * orderPostDTO) {
     * // Order createdOrder =
     * profileService.createOrder(orderRequestDTO.getItems());
     * return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
     * }
     */

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
