package com.lbadvisors.pffc.customers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "/sales-reps/{id}/customers")
    public ResponseEntity<List<CustomerGetDto>> getAllCustomers(@PathVariable("id") String salesRepName) {

        return new ResponseEntity<>(
                customerService.getAllCustomers(salesRepName), HttpStatus.OK);
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
