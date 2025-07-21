package com.example.hitachi.test.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationStartupLogger {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupLogger.class);

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public CommandLineRunner logApplicationUrls() {
        return args -> {
            logger.info("Application is running on: http://localhost:{}", serverPort);
            logger.info("Swagger UI available at: http://localhost:{}/swagger-ui", serverPort);
            logger.info("OpenAPI documentation available at: http://localhost:{}/v3/api-docs", serverPort);
        };
    }
}
