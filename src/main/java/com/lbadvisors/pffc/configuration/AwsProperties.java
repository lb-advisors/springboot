package com.lbadvisors.pffc.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import lombok.Data;

@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {
    private String endpointUrl;
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String region;
}
