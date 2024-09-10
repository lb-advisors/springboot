package com.lbadvisors.pffc.poc_authy;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.stereotype.Component;

import com.lbadvisors.pffc.configuration.JwtProperties;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

@Component
public class JwtUtils {

    @Autowired
    private JwtProperties jwtProperties;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromClaims(Map<String, Object> claims) {
        return (List<String>) claims.get("roles"); // Suppress warning if cast is known to be safe
    }

    public String generateAuthenticatedToken(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        claims.put("roles", roles);
        return Jwts.builder().subject(userDetails.getUsername()).claims(claims).issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + Long.parseLong(jwtProperties.getJwtExpirationInMs()))).signWith(key).compact();
    }

    public String generateResetPasswordToken(String username) {
        return Jwts.builder().subject(username).issuedAt(new Date()).expiration(new Date((new Date()).getTime() + Long.parseLong(jwtProperties.getJwtExpirationInMs())))
                .signWith(key).compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String authToken) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken).getPayload();

        List<String> roles = getRolesFromClaims(claims);

        return roles.stream().map(SimpleGrantedAuthority::new) // Convert each string to SimpleGrantedAuthority
                .collect(Collectors.toList()); // Collect into List<SimpleGrantedAuthority>
    }

    public boolean validateJwtToken(String authToken) throws BadCredentialsException, NonceExpiredException {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new SignatureException("Invalid token", ex);
        } catch (ExpiredJwtException ex) {
            throw new NonceExpiredException("Token has Expired", ex);
        }
    }
}