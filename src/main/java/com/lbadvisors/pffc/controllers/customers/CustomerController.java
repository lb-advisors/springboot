package com.lbadvisors.pffc.controllers.customers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping(value = "/sales-reps/{id}/customers")
    @Operation(summary = "Get sales rep's list of customers")
    public ResponseEntity<List<CustomerGetDto>> getAllCustomers(@PathVariable("id") String salesRepName) {

        return new ResponseEntity<>(
                customerService.getAllCustomers(salesRepName), HttpStatus.OK);
    }
}