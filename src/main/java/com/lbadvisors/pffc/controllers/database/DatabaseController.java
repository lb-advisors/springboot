package com.lbadvisors.pffc.controllers.database;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.tika.Tika;
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

import com.opencsv.exceptions.CsvValidationException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/database/csv")
public class DatabaseController {

    @Autowired
    private DatabaseService databaseService;

    @Autowired
    private Tika tika;

    @GetMapping("/download/orders")
    @Operation(summary = "Download entire order table as a CSV file")
    public void downloadOrdersCsv(HttpServletResponse response) throws IOException {
        // Set the response headers for CSV download
        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.csv");

        // Write CSV data to the response
        PrintWriter writer = response.getWriter();
        databaseService.writeOrdersToCsv(writer);
    }

    @PostMapping(value = "/upload/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadProfilesFile(
            @Parameter(description = "CSV file with list of all profiles", required = true) @RequestParam("file") MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please upload a CSV file!");
        }

        // Check MIME type (content type)
        String contentType = multipartFile.getContentType();
        if (!"text/csv".equals(contentType)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file type. Please upload a CSV file.");
        }

        // Alternatively, check the file extension (case-insensitive)
        // String originalFilename = multipartFile.getOriginalFilename();
        // if (originalFilename != null &&
        // !originalFilename.toLowerCase().endsWith(".csv")) {
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file
        // extension. Please upload a file with .csv extension.");
        // }

        try {
            databaseService.parseProfilesFromCsvAndSaveData(multipartFile);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded and data stored successfully!");
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the file: " + e.getMessage());
        }
    }
}
