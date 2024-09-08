package com.peecko.api.service;

import com.peecko.api.domain.EntityDefault;
import com.peecko.api.domain.InvalidJwt;
import com.peecko.api.repository.InvalidJwtRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class InvalidJwtServiceTest {

    @Autowired
    InvalidJwtService invalidJwtService;

    @Autowired
    InvalidJwtRepo invalidJwtRepo;

    @Test
    void invalidateJwt() {
        //GIVEN
        invalidJwtService.invalidateJwt(EntityDefault.JTI);

        //WHEN
        Optional<InvalidJwt> invalidJwt = invalidJwtRepo.findByJti(EntityDefault.JTI);

        //THEN
        assertTrue(invalidJwt.isPresent());
    }

}