package com.lbadvisors.pffc.controllers.company;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @GetMapping(value = "/companies")
    @Operation(summary = "Get all companies")
    public ResponseEntity<List<CompanyGetDto>> getCompanies() {
        return new ResponseEntity<>(
                companyService.getAllCompanies(), HttpStatus.OK);
    }

}
