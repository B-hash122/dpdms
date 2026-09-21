package com.dpdms.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka Discovery Server for the Rushinga Provincial Disaster
 * Monitoring and Management System (DPDMS).
 *
 * All microservices (flood-service, drought-service, fire-service,
 * zoonotic-service, mining-service, api-gateway, etc.) register
 * themselves here so they can find one another by name instead of
 * hard-coded host:port values.
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
    }
}
