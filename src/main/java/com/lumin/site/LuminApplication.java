package com.lumin.site;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.TimeZone;

/**
 * Main entry point and Spring Boot bootstrap class for the Lumin Consultancy web portal.
 * <p>
 * Configures the application context, sets the global default JVM timezone to Indian
 * Standard Time (Asia/Kolkata, UTC+5:30), and starts the embedded Apache Tomcat container.
 * </p>
 *
 * @author Lumin Technical Team
 * @version 1.0.0
 */
@SpringBootApplication
public class LuminApplication {

    /**
     * Post-construction lifecycle callback ensuring that all Spring-managed components
     * and database operations observe the Asia/Kolkata timezone uniformly.
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    /**
     * Application entry point for starting the Spring Boot web application.
     *
     * @param args Command-line arguments passed to the application at launch.
     */
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(LuminApplication.class, args);
    }
}
