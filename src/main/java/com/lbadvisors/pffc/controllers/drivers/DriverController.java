package com.lbadvisors.pffc.controllers.drivers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping(value = "")
    @Operation(summary = "Get list of all drivers")
    public ResponseEntity<List<DriverGetDto>> getAllDrivers() {
        return new ResponseEntity<>(
                driverService.findAll(), HttpStatus.OK);
    }
}
