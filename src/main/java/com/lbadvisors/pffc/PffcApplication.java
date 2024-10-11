package com.lbadvisors.pffc;

import org.apache.tika.Tika;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.lbadvisors.pffc.configuration.AuditorAwareImpl;
import com.lbadvisors.pffc.configuration.AwsProperties;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class })
@EnableConfigurationProperties(AwsProperties.class)
@EnableScheduling

public class PffcApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public Tika tika() {
		return new Tika();
	}

	@Configuration
	@EnableJpaAuditing(auditorAwareRef = "auditorAware")
	public class JpaConfig {
		@Bean
		public AuditorAware<String> auditorAware() {
			return new AuditorAwareImpl();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(PffcApplication.class, args);
	}

}
