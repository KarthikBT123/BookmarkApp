package com.example.BookmarkApp;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;
import java.util.stream.Collectors;

public class DotenvConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();

            ConfigurableEnvironment environment = applicationContext.getEnvironment();
            
            Map<String, Object> dotenvMap = dotenv.entries()
                    .stream()
                    .collect(Collectors.toMap(
                            entry -> entry.getKey(),
                            entry -> entry.getValue()
                    ));

            environment.getPropertySources().addFirst(
                    new MapPropertySource("dotenv", dotenvMap)
            );
        } catch (Exception e) {
            // If .env file is not found or there's an error, continue without it
            System.out.println("Could not load .env file: " + e.getMessage());
        }
    }
} 