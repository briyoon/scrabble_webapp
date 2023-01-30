package com.briyoon.scrabbleserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ScrabbleServerApplication {
    @Autowired
    private Environment env;

	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
					.addMapping("/api/**")
					.allowedOrigins(env.getProperty("client_origin"));
            }
        };
	}

	public static void main(String[] args) {
		SpringApplication.run(ScrabbleServerApplication.class, args);
	}

}
