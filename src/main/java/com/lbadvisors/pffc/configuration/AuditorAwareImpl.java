package com.lbadvisors.pffc.configuration;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lbadvisors.pffc.util.HttpUtils;

import jakarta.servlet.http.HttpServletRequest;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        String callerIp = getCallerIp();
        return Optional.of(callerIp);
        // Can use Spring Security to return currently logged in user
        // return ((User)
        // SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()
    }

    private String getCallerIp() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String ip = HttpUtils.getRequestIP(request);
            return ip;

        }
        return null;
    }
}