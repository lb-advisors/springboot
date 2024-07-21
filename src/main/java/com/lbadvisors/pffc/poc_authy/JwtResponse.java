
package com.lbadvisors.pffc.poc_authy;

import org.springframework.security.core.GrantedAuthority;
import lombok.Data;
import java.util.Collection;

@Data
public class JwtResponse {
    private String token;
    private Long userid;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtResponse(String token, Long userid, String username, Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.userid = userid;
        this.username = username;
        this.authorities = authorities;
    }
}
