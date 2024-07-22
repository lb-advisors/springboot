package com.lbadvisors.pffc.poc_authy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lbadvisors.pffc.controllers.inventory.Inventory;
import com.lbadvisors.pffc.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.lbadvisors.pffc.repositories.UserRepository;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/auth/login")
    public JwtResponse authenticateUser(@RequestBody LoginPostDto loginRequest) {

        System.out.println(passwordEncoder.encode("123"));

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getAuthorities());
    }

    @PostMapping("/auth/new-password")
    public ResponseEntity<Map<String, String>> resetPAssword(@RequestBody LoginPostDto loginRequest) {

        String username = loginRequest.getUsername();
        String newPassword = passwordEncoder.encode(loginRequest.getPassword());

        User user = userRepository.findByUsername(username);

        user.setPassword(newPassword);
        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed");
        response.put("status", "success");
        // You can add more key-value pairs as needed

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}