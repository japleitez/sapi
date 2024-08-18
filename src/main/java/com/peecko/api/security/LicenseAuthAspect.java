package com.peecko.api.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LicenseAuthAspect {

    final LicenseService licenseService;

    public LicenseAuthAspect(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @Around("@annotation(com.peecko.api.security.Licensed)")
    public Object validate(ProceedingJoinPoint call) throws Throwable {
        if (licenseService.authorize()) {
            return call.proceed();
        } else {
            throw new AccessDeniedException("License is invalid or expired.");
        }
    }
}
