package com.peecko.api.service;

import com.peecko.api.domain.InvalidJwt;
import com.peecko.api.repository.InvalidJwtRepo;
import com.peecko.api.security.JwtUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InvalidJwtService {
    final JwtUtils jwtUtils;
    final InvalidJwtRepo invalidJwtRepo;

    public InvalidJwtService(JwtUtils jwtUtils, InvalidJwtRepo invalidJwtRepo) {
        this.jwtUtils = jwtUtils;
        this.invalidJwtRepo = invalidJwtRepo;
    }

    public void invalidateJwt(String jti) {
        InvalidJwt invalidJwt = new InvalidJwt();
        invalidJwt.setJti(jti);
        invalidJwt.setInvalidatedAt(LocalDateTime.now());
        invalidJwtRepo.save(invalidJwt);
    }

    public boolean isJwtInvalid(String jti) {
        return invalidJwtRepo.findByJti(jti).isPresent();
    }

}
