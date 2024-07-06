package com.lbadvisors.pffc.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentChecker {

    @Autowired
    private Environment environment;

    public boolean isLocal() {
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equalsIgnoreCase("local")) {
                return true;
            }
        }
        return false;
    }

    public boolean isDev() {
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equalsIgnoreCase("dev")) {
                return true;
            }
        }
        return false;
    }

    public boolean isProd() {
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equalsIgnoreCase("prod")) {
                return true;
            }
        }
        return false;
    }
}