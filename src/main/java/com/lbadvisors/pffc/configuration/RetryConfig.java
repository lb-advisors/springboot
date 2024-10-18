package com.lbadvisors.pffc.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry(proxyTargetClass = false) // default value
public class RetryConfig {
}