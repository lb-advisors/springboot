package com.lbadvisors.pffc.poc_authy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lbadvisors.pffc.exception.ResponseMessage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/auth/login")
    @Operation(summary = "Log in to retrieve JWT token")
    public JwtResponse authenticateUser(@RequestBody AuthenticationPostDto authenticationRequest) {

        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException ex) {
            customUserDetailsService.incrementFailedLoginAttemptsCount(authenticationRequest.getUsername());
            throw ex;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateAuthenticatedToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        customUserDetailsService.incrementLoginCount(userDetails.getUsername());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getAuthorities());
    }

    @PostMapping("/auth/request-password-reset")
    @Operation(summary = "Request an email to reset the password")
    public ResponseEntity<ResponseMessage> requestPasswordReset(@RequestBody PasswordRequestPostDto passwordRequestPostDto) {
        customUserDetailsService.sendPasswordResetEmail(passwordRequestPostDto.getUsername());
        ResponseMessage message = new ResponseMessage(HttpStatus.OK.value(), "An email has been sent.", "An email has been sent to " + passwordRequestPostDto.getUsername());
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/auth/new-password")
    @Operation(summary = "Reset the password")
    public ResponseEntity<ResponseMessage> resetPassword(@RequestBody PasswordResetPostDto passwordResetPostDto) {
        customUserDetailsService.resetPassword(passwordResetPostDto.getUsername(), passwordResetPostDto.getPassword(), passwordResetPostDto.getToken());
        ResponseMessage message = new ResponseMessage(HttpStatus.OK.value(), "The password has been updated",
                "The password for user " + passwordResetPostDto.getUsername() + " has been updated");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PostMapping("/auth/users")
    @Operation(summary = "Create a new user account")
    public ResponseEntity<UserGetDto> createUserAccount(@RequestBody NewUserPostDto newUserPostDto) {
        UserGetDto user = customUserDetailsService.createUserAccount(newUserPostDto);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/auth/users")
    @Operation(summary = "Get all user accounts")
    public ResponseEntity<List<UserGetDto>> getAllUsers() {
        List<UserGetDto> users = customUserDetailsService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}