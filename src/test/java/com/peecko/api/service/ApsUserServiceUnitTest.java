package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.repository.ApsUserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ApsUserServiceUnitTest {

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    ApsUserService apsUserService;

    @Test
    void exists() {
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);
        assertTrue(apsUserService.exists(apsUser.getUsername()));
        apsUserRepo.delete(apsUser);
    }

}