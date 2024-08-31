package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.domain.EntityDefault;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.utils.NameUtils;
import com.peecko.api.web.payload.request.SignUpRequest;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ApsUserServiceTest {

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    ApsUserService apsUserService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void signUp() {
        //GIVEN
        SignUpRequest signUpRequest = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(signUpRequest);

        //WHEN
        ApsUser apsUser = apsUserRepo.findByUsername(signUpRequest.username()).orElseThrow();

        //THEN
        assertEquals(signUpRequest.username(), apsUser.getUsername());
        assertEquals(NameUtils.toCamelCase(signUpRequest.name()), apsUser.getName());
        assertTrue(passwordEncoder.matches(signUpRequest.password(), apsUser.getPassword()));
        assertEquals(signUpRequest.language(), apsUser.getLanguage().name());
        assertNull(apsUser.getLicense());
        apsUserRepo.delete(apsUser);

    }

    @Test
    void authenticated() {
    }

    @Test
    void exists() {
        //GIVEN
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);
        apsUserRepo.flush();

        //WHEN
        boolean exists = apsUserService.exists(apsUser.getUsername());

        //THEN
        assertTrue(exists);
        apsUserRepo.delete(apsUser);
    }

    @Test
    void doesNotExist() {
    }

    @Test
    void setUserActive() {
    }

    @Test
    void setUserLanguage() {
    }


    @Test
    void signIn() {
    }

    @Test
    void getProfile() {
    }

    @Test
    void signOut() {
    }

    @Test
    void getUserDevicesAsDTO() {
    }

    @Test
    void updateUserPassword() {
    }

    @Test
    void updateUserName() {
    }

    @Test
    void passwordDoesNotMatch() {
    }
}