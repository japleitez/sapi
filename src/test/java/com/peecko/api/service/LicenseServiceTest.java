package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.domain.EntityDefault;
import com.peecko.api.repository.ApsMembershipRepo;
import com.peecko.api.repository.ApsUserRepo;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;


@SpringBootTest
class LicenseServiceTest {

    @Autowired
    ApsMembershipRepo apsMembershipRepo;

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    LicenseService licenseService;

    @BeforeEach
    void beforeEach() {
        apsMembershipRepo.deleteAll();
        apsMembershipRepo.flush();
        apsUserRepo.deleteAll();
        apsUserRepo.flush();
    }

    @Test
    void isAuthorized() {
        // given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUser.license(EntityDefault.LICENSE);
        apsUserRepo.save(apsUser);
        apsUserRepo.flush();

        apsMembershipRepo.save(EntityBuilder.buildApsMembership());
        apsMembershipRepo.flush();

        // when
        boolean isAuthorized = licenseService.isAuthorized(apsUser.getUsername());

        // then
        assertTrue(isAuthorized);
    }

    @Test
    void isNotAuthorizedWithoutLicense() {
        // given a user with no license
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUser.license(null);
        apsUserRepo.save(apsUser);
        apsUserRepo.flush();

        apsMembershipRepo.save(EntityBuilder.buildApsMembership());
        apsMembershipRepo.flush();

        // when
        boolean isAuthorized = licenseService.isAuthorized(apsUser.getUsername());

        // then
        assertFalse(isAuthorized);
    }

    @Test
    void isNotAuthorizedWithoutMembership() {
        // given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUser.license(EntityDefault.LICENSE);
        apsUserRepo.save(apsUser);
        apsUserRepo.flush();

        // when
        boolean isAuthorized = licenseService.isAuthorized(apsUser.getUsername());

        // then
        assertFalse(isAuthorized);
    }

}