package com.lbadvisors.pffc.drivers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping(value = "")
    public ResponseEntity<List<String>> getAllDrivers() {
        return new ResponseEntity<List<String>>(
                driverService.findAll(), HttpStatus.OK);
    }
}
