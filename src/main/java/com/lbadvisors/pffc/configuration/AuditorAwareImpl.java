package com.lbadvisors.pffc.configuration;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        return Optional.of("Olivier");
        // Can use Spring Security to return currently logged in user
        // return ((User)
        // SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()
    }
}