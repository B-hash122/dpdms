package com.dpdms.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Single entry point for all client traffic into the DPDMS.
 * Routes are configured declaratively in application.yml and resolved
 * dynamically via Eureka service discovery (lb://SERVICE-NAME).
 *
 * Security note (see Anesu's guide): Shebe pushes the JWT / route
 * filter configuration for this module — pull those changes before
 * exposing this gateway publicly.
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
