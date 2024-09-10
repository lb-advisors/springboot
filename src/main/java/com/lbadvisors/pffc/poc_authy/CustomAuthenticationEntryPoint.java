package com.lbadvisors.pffc.poc_authy;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lbadvisors.pffc.exception.ResponseMessage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        final String expiredTokenMsg = (String) request.getAttribute("expired");
        String msg;
        if (expiredTokenMsg != null) {
            msg = expiredTokenMsg;
        } else {
            msg = authException.getMessage();
        }

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ObjectMapper objectMapper = new ObjectMapper();
        ResponseMessage message = new ResponseMessage(HttpServletResponse.SC_UNAUTHORIZED, msg, "You are not authorized to access " + request.getRequestURI());
        response.getOutputStream().println(objectMapper.writeValueAsString(message));

    }
}