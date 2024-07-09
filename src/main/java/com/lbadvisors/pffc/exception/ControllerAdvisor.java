package com.lbadvisors.pffc.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolationException;

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

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @ExceptionHandler(value = { ResponseStatusException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorMessage> resourceNotFoundException(ResponseStatusException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "Resource Not Found");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleConstraintViolationException(ConstraintViolationException ex) {

        StringBuilder errroMessage = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> {
            errroMessage.append(violation.getMessage()).append("; ");
        });

        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                errroMessage.toString(),
                "Constraints violation");

        logger.error(message.getMessage());

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleConstraintViolationException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errrorMessage = errors.entrySet()
                .stream()
                .map(entry -> entry.getValue())
                .collect(Collectors.joining("; "));

        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                errrorMessage,
                "Invalid parameters");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "Invalid parameters");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(DataIntegrityViolationException ex) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.CONFLICT.value(),
                "Data integrity violation occurred: " + ex.getMostSpecificCause().getMessage(),
                "Invalid parameters");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex) {

        ex.printStackTrace();

        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                "Runtime exception");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}