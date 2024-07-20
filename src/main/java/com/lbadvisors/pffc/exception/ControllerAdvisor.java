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
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<ErrorMessage> handleEntityNotFoundException(EntityNotFoundException ex) {
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage(), "Resource Not Found");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { ResourceAlreadyExistsException.class })
    public ResponseEntity<ErrorMessage> handleeResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        ErrorMessage message = new ErrorMessage(HttpStatus.CONFLICT.value(), ex.getMessage(), "Resource already exists");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorMessage> handleConstraintViolationException(ConstraintViolationException ex) {

        StringBuilder errroMessage = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> {
            errroMessage.append(violation.getMessage()).append("; ");
        });

        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), errroMessage.toString(), "Constraints violation");

        logger.error(message.getMessage(), ex);

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleeMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errrorMessage = errors.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.joining("; "));

        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), errrorMessage, "Invalid parameters");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), "Invalid parameters");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.BAD_REQUEST);
    }

    // not being received by server - 413 error behaves like this
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(MaxUploadSizeExceededException ex) {
        ErrorMessage message = new ErrorMessage(HttpStatus.PAYLOAD_TOO_LARGE.value(), ex.getMessage(), "The file size is too large");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(DataIntegrityViolationException ex) {
        ErrorMessage message = new ErrorMessage(HttpStatus.CONFLICT.value(), "Data integrity violation occurred: " + ex.getMostSpecificCause().getMessage(), "Invalid parameters");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex) {

        ex.printStackTrace();

        ErrorMessage message = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), "Runtime exception");

        return new ResponseEntity<ErrorMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}