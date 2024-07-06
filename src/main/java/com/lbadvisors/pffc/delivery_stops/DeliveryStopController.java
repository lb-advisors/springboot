package com.lbadvisors.pffc.delivery_stops;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@CrossOrigin
@RequestMapping("/delivery-stops")
public class DeliveryStopController {

    @Autowired
    private DeliveryStopService deliveryStopsService;

    @GetMapping(value = "")
    @Operation(summary = "Get all delivery stops given a driver and a date")
    public ResponseEntity<List<DeliveryStopGetDto>> getAllDeliveryStops(
            @Parameter(description = "Driver name", required = true) @RequestParam String driverName,
            @Parameter(description = "Delivery date (yyyy-mm-dd)", required = true) @RequestParam LocalDate deliveryDate) {

        return new ResponseEntity<>(
                deliveryStopsService.findByDriverNameAndDeliveryDate(driverName, deliveryDate), HttpStatus.OK);
    }

    @Operation(summary = "Upload a photo")
    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DeliveryStopGetDto> uploadFile(
            @Parameter(description = "Delivery stop id", required = true) @PathVariable("id") int id,
            @Parameter(description = "Photo to upload (jpg, gif, bmp...)", required = true) @RequestParam("file") MultipartFile multipartFile) {

        DeliveryStopGetDto deliveryStopGetDto = deliveryStopsService.uploadPhoto(id, multipartFile);

        return ResponseEntity.ok(deliveryStopGetDto);
    }

    // @DeleteMapping("/deleteFile")
    // public String deleteFile(@RequestPart(value = "url") String fileUrl) {
    // return this.amazonClient.deleteFileFromS3Bucket(fileUrl);
    // }

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
