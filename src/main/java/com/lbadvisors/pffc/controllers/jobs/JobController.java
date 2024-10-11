package com.lbadvisors.pffc.controllers.jobs;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lbadvisors.pffc.exception.StatusMessage;
import com.opencsv.exceptions.CsvValidationException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private GoogleDriveService googleDriveService;

    @GetMapping("/test")
    @Operation(summary = "Download entire order table as a CSV file")
    public String test(HttpServletResponse response) throws IOException, GeneralSecurityException {
        // String listOfFiles = googleDriveService.readCsvFileByName("test.csv");
        // return listOfFiles;

        googleDriveService.refreshProfileTableFromCsv();
        return "All good";
    }

}
