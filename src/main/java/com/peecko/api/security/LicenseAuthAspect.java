package com.peecko.api.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;

@Aspect
@Configuration
public class LicenseAuthAspect {

    @Autowired
    LicenseServiceImpl licenseService;

    @Around("@annotation(com.peecko.api.security.Licensed)")
    public Object validate(ProceedingJoinPoint call) throws Throwable {
        if (licenseService.authorize()) {
            return call.proceed();
        } else {
            throw new AccessDeniedException("License is invalid or expired.");
        }
    }
}
