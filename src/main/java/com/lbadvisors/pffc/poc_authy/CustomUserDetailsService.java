package com.lbadvisors.pffc.poc_authy;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.lbadvisors.pffc.entities.Role;
import com.lbadvisors.pffc.entities.User;
import com.lbadvisors.pffc.exception.ResourceAlreadyExistsException;
import com.lbadvisors.pffc.repositories.UserRepository;
import com.lbadvisors.pffc.util.EmailService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RoleService roleService;

    @Autowired
    private EmailService emailService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    // @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserDetailsImpl.build(user);
    }

    public List<UserGetDto> getAllUsers() {
        return this.userRepository.findAll().stream().map((user) -> modelMapper.map(user, UserGetDto.class)).collect(Collectors.toList());
    }

    public void incrementLoginCount(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setLoginCount(user.getLoginCount() + 1);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void incrementFailedLoginAttemptsCount(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setFailedLoginAttemptsCount(user.getFailedLoginAttemptsCount() + 1);
        userRepository.save(user);
    }

    @Transactional
    public void resetPassword(String username, String password, String token) {

        // validate token
        jwtUtils.validateJwtToken(token);

        String usernameFromToken = jwtUtils.getUserNameFromJwtToken(token);

        if (!username.equals(usernameFromToken)) {
            throw new IllegalArgumentException("The email address does not match the one found in the token.");
        }

        // if token valid
        String newPassword = passwordEncoder.encode(password);

        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Username " + username + " not found"));

        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public void sendPasswordResetEmail(String username) {

        userRepository.findByUsername(username).orElseThrow(() -> {
            throw new ResourceAlreadyExistsException("Username " + username + " does not exists.");
        });

        String token = jwtUtils.generateResetPasswordToken(username);

        System.out.println(token);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("token", token);
        emailService.sendResetPasswordEmail("", templateModel);
    }

    public UserGetDto createUserAccount(NewUserPostDto newUserPostDto) {

        String username = newUserPostDto.getUsername();

        userRepository.findByUsername(username).ifPresent((user) -> {
            throw new ResourceAlreadyExistsException("Username " + user.getUsername() + " already exists.");
        });

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString())); // temporary password
        user.setFirstName(newUserPostDto.getFirstName());
        user.setLastName(newUserPostDto.getLastName());
        user.setEmail(username);

        user.setFailedLoginAttemptsCount(0L);
        user.setLoginCount(0L);
        user.setEnabled(true);
        user.setLocked(false);

        List<Role> roles = new ArrayList<Role>();
        for (Long roleId : newUserPostDto.getRoleIds()) {
            roleService.getRoleById(roleId).ifPresent(role -> roles.add(role));
        }

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        sendPasswordResetEmail(username);

        return modelMapper.map(savedUser, UserGetDto.class);

    }
}