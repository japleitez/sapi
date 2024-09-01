package com.peecko.api.service;

import com.peecko.api.domain.ApsMembership;
import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.domain.EntityDefault;
import com.peecko.api.repository.ApsMembershipRepo;
import com.peecko.api.repository.ApsUserRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    ApsMembershipRepo apsMembershipRepo;

    @Autowired
    AccountService accountService;

    @Test
    void testActivateUserLicense_Success() {
        //GIVEN
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUser.license(null);
        apsUserRepo.save(apsUser);
        apsUserRepo.flush();

        ApsMembership aspsMembership = EntityBuilder.buildApsMembership();
        aspsMembership.setUsername(apsUser.getUsername());
        aspsMembership.setLicense(EntityDefault.LICENSE);
        apsMembershipRepo.save(aspsMembership);
        apsMembershipRepo.flush();

        //WHEN
        boolean activated = accountService.activateUserLicense(apsUser.getUsername(), aspsMembership.getPeriod(), aspsMembership.getLicense());

        //THEN
        assertTrue(activated);
        ApsUser updated = apsUserRepo.findByUsername(apsUser.getUsername()).orElseThrow();
        Assertions.assertEquals(EntityDefault.LICENSE, updated.getLicense());
        
    }

}