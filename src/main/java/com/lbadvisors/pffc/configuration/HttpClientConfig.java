package com.lbadvisors.pffc.configuration;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    @Bean
    public CloseableHttpClient closeableHttpClient() {
        // Create RequestConfig with timeouts
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(5000) // 5 seconds connection timeout
                .setSocketTimeout(10000) // 10 seconds socket timeout
                .build();

        // Create BasicHttpClientConnectionManager
        BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager();

        // Build the CloseableHttpClient with RequestConfig and ConnectionManager
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connectionManager).build();
    }
}
