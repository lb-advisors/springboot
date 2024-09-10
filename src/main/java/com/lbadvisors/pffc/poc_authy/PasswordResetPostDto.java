package com.lbadvisors.pffc.poc_authy;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordResetPostDto {
    @NotNull(message = "Username is required")
    String username;
    @NotNull(message = "Password is required")
    String password;
    @NotNull(message = "Token is required")
    String token;
}
