package com.lbadvisors.pffc.exception;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.opencsv.exceptions.CsvValidationException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<StatusMessage> handleEntityNotFoundException(EntityNotFoundException ex) {
        StatusMessage message = new StatusMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage(), "Resource Not Found");

        return new ResponseEntity<StatusMessage>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { ResourceAlreadyExistsException.class })
    public ResponseEntity<StatusMessage> handleeResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        StatusMessage message = new StatusMessage(HttpStatus.CONFLICT.value(), ex.getMessage(), "Resource already exists");

        return new ResponseEntity<StatusMessage>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StatusMessage> handleConstraintViolationException(ConstraintViolationException ex) {

        StringBuilder errroMessage = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> {
            errroMessage.append(violation.getMessage()).append("; ");
        });

        StatusMessage message = new StatusMessage(HttpStatus.BAD_REQUEST.value(), errroMessage.toString(), "Constraints violation");

        logger.error(message.getMessage(), ex);

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StatusMessage> handleeMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String StatusMessage = error.getDefaultMessage();
            errors.put(fieldName, StatusMessage);
        });

        String errrorMessage = errors.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.joining("; "));

        StatusMessage message = new StatusMessage(HttpStatus.BAD_REQUEST.value(), errrorMessage, "Invalid parameters");

        return new ResponseEntity<StatusMessage>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StatusMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
        StatusMessage message = new StatusMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), "Invalid parameters");

        return new ResponseEntity<StatusMessage>(message, HttpStatus.BAD_REQUEST);
    }

    // not being received by server - 413 error behaves like this
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<StatusMessage> handleIllegalArgumentException(MaxUploadSizeExceededException ex) {
        StatusMessage message = new StatusMessage(HttpStatus.PAYLOAD_TOO_LARGE.value(), ex.getMessage(), "The file size is too large");

        return new ResponseEntity<StatusMessage>(message, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StatusMessage> handleIllegalArgumentException(DataIntegrityViolationException ex) {
        StatusMessage message = new StatusMessage(HttpStatus.CONFLICT.value(), "Data integrity violation occurred: " + ex.getMostSpecificCause().getMessage(),
                "Invalid parameters");

        return new ResponseEntity<StatusMessage>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ IOException.class, CsvValidationException.class })
    public ResponseEntity<StatusMessage> handleIoException(Exception ex) {
        StatusMessage message = new StatusMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error processing file", "Error occurred while processing the file:" + ex.getMessage());
        return new ResponseEntity<StatusMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StatusMessage> globalExceptionHandler(Exception ex) {

        ex.printStackTrace();

        StatusMessage message = new StatusMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), "Runtime exception");

        return new ResponseEntity<StatusMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}