package com.lbadvisors.pffc.controllers.profiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Operation(summary = "Get all profiles related to a customer")
    @GetMapping(value = "/customers/{id}/profiles")
    public ResponseEntity<ProfileGetDto> getProfiles(@PathVariable("id") int customerId) {

        return new ResponseEntity<ProfileGetDto>(
                profileService.findByCustomerId(customerId), HttpStatus.OK);
    }
}