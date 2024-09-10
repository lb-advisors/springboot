package com.lbadvisors.pffc.poc_authy;

import java.util.Set;
import lombok.Data;

@Data
public class UserGetDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean isEnabled;
    private boolean isLocked;
    private Set<RoleGetDto> roles;

}
