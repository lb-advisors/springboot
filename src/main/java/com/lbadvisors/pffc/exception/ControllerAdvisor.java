package com.lbadvisors.pffc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ControllerAdvisor {

    /*
     * @ExceptionHandler(CityNotFoundException.class)
     * public ResponseEntity<Object> handleCityNotFoundException(
     * CityNotFoundException ex, WebRequest request) {
     * 
     * Map<String, Object> body = new LinkedHashMap<>();
     * body.put("timestamp", .now());
     * body.put("message", "City not found");
     * 
     * return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
     * }
     * 
     * @ExceptionHandler(NoDataFoundException.class)
     * public ResponseEntity<Object> handleNodataFoundException(
     * NoDataFoundException ex, WebRequest request) {
     * 
     * Map<String, Object> body = new LinkedHashMap<>();
     * body.put("timestamp", LocalDateTime.now());
     * body.put("message", "No cities found");
     * 
     * return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
     * }
     */

    @ExceptionHandler(value = { ResponseStatusException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(ResponseStatusException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "Resource Not Found");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                "Runtime exception");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}