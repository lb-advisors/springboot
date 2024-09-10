package com.lbadvisors.pffc.poc_authy;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordRequestPostDto {
    @NotNull(message = "Username is required")
    String username;
}
