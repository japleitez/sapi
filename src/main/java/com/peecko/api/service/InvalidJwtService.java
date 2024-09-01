package com.peecko.api.service;

import com.peecko.api.domain.InvalidJwt;
import com.peecko.api.repository.InvalidJwtRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InvalidJwtService {
    final InvalidJwtRepo invalidJwtRepo;

    public InvalidJwtService(InvalidJwtRepo invalidJwtRepo) {
        this.invalidJwtRepo = invalidJwtRepo;
    }

    public void invalidateJwt(String jti) {
        InvalidJwt invalidJwt = new InvalidJwt();
        invalidJwt.setJti(jti);
        invalidJwt.setInvalidatedAt(Instant.now());
        invalidJwtRepo.save(invalidJwt);
    }

}
