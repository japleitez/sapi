package com.peecko.api.web.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint for load balancer and monitoring
 * Used by AWS ALB to verify instance health
 */
@RestController
@RequestMapping("/health")
public class HealthResource {

    /**
     * Simple health check endpoint
     * Returns 200 OK with "ok" message
     *
     * @return ResponseEntity with status 200 and body "ok"
     */
    @GetMapping
    public ResponseEntity<String> health() {
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }

}
