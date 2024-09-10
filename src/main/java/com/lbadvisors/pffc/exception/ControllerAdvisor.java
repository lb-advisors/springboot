package com.lbadvisors.pffc.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import io.jsonwebtoken.security.SignatureException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class ControllerAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(ControllerAdvisor.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleEntityNotFoundException(EntityNotFoundException ex) {
        ResponseMessage message = new ResponseMessage(HttpStatus.NOT_FOUND.value(), ex.getMessage(), "Resource Not Found");

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ResponseMessage> handleeResourceAlreadyExistsException(ResourceAlreadyExistsException ex) {
        ResponseMessage message = new ResponseMessage(HttpStatus.CONFLICT.value(), ex.getMessage(), "Resource already exists");

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseMessage> handleConstraintViolationException(ConstraintViolationException ex) {

        StringBuilder errroMessage = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> {
            errroMessage.append(violation.getMessage()).append("; ");
        });

        ResponseMessage message = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), errroMessage.toString(), "Constraints violation");

        logger.error(message.getMessage(), ex);

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage> handleeMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errrorMessage = errors.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.joining("; "));

        ResponseMessage message = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), errrorMessage, "Invalid parameters");

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseMessage> handleIllegalArgumentException(IllegalArgumentException ex) {
        ResponseMessage message = new ResponseMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), "Invalid parameters");

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.BAD_REQUEST);
    }

    // not being received by server - 413 error behaves like this
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseMessage> handleIllegalArgumentException(MaxUploadSizeExceededException ex) {
        ResponseMessage message = new ResponseMessage(HttpStatus.PAYLOAD_TOO_LARGE.value(), ex.getMessage(), "The file size is too large");

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseMessage> handleIllegalArgumentException(DataIntegrityViolationException ex) {
        ResponseMessage message = new ResponseMessage(HttpStatus.CONFLICT.value(), "Data integrity violation occurred: " + ex.getMostSpecificCause().getMessage(),
                "Invalid parameters");

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseMessage> handleBadCredentialsException(BadCredentialsException ex) {

        ResponseMessage message = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), "Bad credentials");

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ResponseMessage> handleUsernameNotFoundException(UsernameNotFoundException ex) {

        ResponseMessage message = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), "The username was not found");

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ResponseMessage> handleDisabledException(DisabledException ex) {
        ResponseMessage message = new ResponseMessage(HttpStatus.FORBIDDEN.value(), ex.getMessage(), "Disabled account");
        return new ResponseEntity<ResponseMessage>(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ResponseMessage> handleDisabledException(LockedException ex) {
        ResponseMessage message = new ResponseMessage(HttpStatus.LOCKED.value(), ex.getMessage(), "Locked account");
        return new ResponseEntity<ResponseMessage>(message, HttpStatus.LOCKED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ResponseMessage> handleSignatureException(SignatureException ex) {
        ResponseMessage message = new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), ex.getMessage(), "The token received is invalid.");
        return new ResponseEntity<ResponseMessage>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage> globalExceptionHandler(Exception ex) {

        ex.printStackTrace();

        ResponseMessage message = new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), "Runtime exception");

        return new ResponseEntity<ResponseMessage>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}