package com.lbadvisors.pffc;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.lbadvisors.pffc.configuration.AuditorAwareImpl;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class })
public class PffcApplication {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
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
