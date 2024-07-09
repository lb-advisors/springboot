package com.lbadvisors.pffc.controllers.sales_reps;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class SalesRepController {

    @Autowired
    private SalesRepService salesRepService;

    @GetMapping(value = "/companies/{id}/sales-reps")
    @Operation(summary = "Get all sales reps")
    public ResponseEntity<List<SalesRepGetDto>> getSalesRep(@PathVariable("id") Integer companyId) {
        return new ResponseEntity<>(
                salesRepService.getSalesReps(companyId), HttpStatus.OK);
    }

}
