package com.peecko.api.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint for load balancer and monitoring
 * Used by AWS ALB to verify instance health
 */
@RestController
public class HealthResource {

    private static final Logger log = LoggerFactory.getLogger(HealthResource.class);

    @Value("${app.version}")
    private String version;

    /**
     * Simple health check endpoint
     * Returns 200 OK with "ok" message
     *
     * @return ResponseEntity with status 200 and body "ok"
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("health");
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

    /**
     * Returns the current API version as specified in the pom.xml
     *
     * @return ResponseEntity with status 200 and the version as body
     */
    @GetMapping("/version")
    public ResponseEntity<String> version() {
        log.info("version");
        return ResponseEntity.status(HttpStatus.OK).body(version);
    }

}