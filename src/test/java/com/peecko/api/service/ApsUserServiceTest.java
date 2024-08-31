package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.repository.ApsUserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ApsUserServiceTest {

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    ApsUserService apsUserService;

    final static String DEFAULT_USERNAME = "default@gmail.com";

    @Test
    void exists() {
        ApsUser apsUser = new ApsUser();
        apsUser.username(DEFAULT_USERNAME);
        apsUserRepo.save(apsUser);
        assertTrue(apsUserService.exists(DEFAULT_USERNAME));
        apsUserRepo.delete(apsUser);
    }
}