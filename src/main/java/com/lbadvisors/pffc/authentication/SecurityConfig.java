package com.lbadvisors.pffc.authentication;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@Configuration
//@EnableWebSecurity
public class SecurityConfig {

        // @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(
                                                authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                                                .requestMatchers("/**").authenticated())
                                .httpBasic(Customizer.withDefaults())
                                .sessionManagement(
                                                httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(new AuthenticationFilter(),
                                                UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

}