package com.lbadvisors.pffc.poc_authy;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Parameters to create a new user account")
public class NewUserPostDto {
    @Schema(description = "User first name")
    @NotNull(message = "First name is required")
    private String firstName;
    @Schema(description = "User last name")
    @NotNull(message = "Last name is required")
    private String lastName;
    @Schema(description = "Username / email")
    @NotNull(message = "Username/Email is required")
    private String username;
    @Schema(description = "Array of role IDs")
    @NotNull(message = "Roles are required")
    @NotEmpty(message = "Roles cannot be empty")
    private Set<Long> roleIds;

}
