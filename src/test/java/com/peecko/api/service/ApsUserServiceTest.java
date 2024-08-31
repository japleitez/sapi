package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.domain.EntityDefault;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.utils.NameUtils;
import com.peecko.api.web.payload.request.SignInRequest;
import com.peecko.api.web.payload.request.SignUpRequest;
import com.peecko.api.web.payload.response.UserProfileResponse;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
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


    @BeforeEach
    void beforeEach() {
        apsUserRepo.deleteAll();
        apsUserRepo.flush();
    }

    @Test
    void signUp() {
        //GIVEN
        SignUpRequest request = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(request);

        //WHEN
        ApsUser apsUser = apsUserRepo.findByUsername(request.username()).orElseThrow();

        //THEN
        assertFalse(apsUser.getActive());
        assertNull(apsUser.getLicense());
        assertEquals(request.username(), apsUser.getUsername());
        assertEquals(NameUtils.toCamelCase(request.name()), apsUser.getName());
        assertEquals(request.language(), apsUser.getLanguage().name());
        assertTrue(passwordEncoder.matches(request.password(), apsUser.getPassword()));
    }

    @Test
    void signIn() {
        //GIVEN
        SignUpRequest signUpRequest = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(signUpRequest);

        //WHEN
        SignInRequest signInRequest = EntityBuilder.buildSignInRequest();
        boolean authenticated = apsUserService.authenticated(signUpRequest.username(), signInRequest.password());
        UserProfileResponse response = apsUserService.signIn(signInRequest);

        //THEN
        assertTrue(authenticated);
        assertTrue(response.isEmailVerified());
        assertFalse(response.isDevicesExceeded());
        assertFalse(response.isMembershipActivated());
        assertEquals(1, response.getDevicesCount());
        assertEquals(EntityDefault.USERNAME, response.getUsername());

        ApsUser apsUser = apsUserRepo.findByUsername(signUpRequest.username()).orElseThrow();
        apsUserRepo.delete(apsUser);
        apsUserRepo.flush();

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
    }

    @Test
    void doesNotExist() {
        //GIVEN
        String username = "invalid@mail.com";

        //WHEN
        boolean exists = apsUserService.exists(username);

        //THEN
        assertFalse(exists);
    }

    @Test
    void setUserActive() {
        //GIVEN
        SignUpRequest request = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(request);

        //WHEN
        ApsUser created = apsUserRepo.findByUsername(request.username()).orElseThrow();
        //THEN
        assertFalse(created.getActive());

        //WHEN
        apsUserService.setUserActive(request.username(), true);
        //THEN
        ApsUser updated = apsUserRepo.findByUsername(request.username()).orElseThrow();
        assertTrue(updated.getActive());

    }

    @Test
    void setUserLanguage() {
        //GIVEN
        SignUpRequest request = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(request);

        //WHEN
        ApsUser created = apsUserRepo.findByUsername(request.username()).orElseThrow();
        //THEN
        assertEquals(request.language(), created.getLanguage().name());

        //WHEN
        Lang otherLanguage = Lang.FR;
        apsUserService.setUserLanguage(request.username(), otherLanguage);
        //THEN
        ApsUser updated = apsUserRepo.findByUsername(request.username()).orElseThrow();
        assertEquals(otherLanguage, updated.getLanguage());

    }


    @Test
    void getProfile() {
        //GIVEN
        SignUpRequest request = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(request);

        SignInRequest signInRequest = EntityBuilder.buildSignInRequest();
        apsUserService.signIn(signInRequest);

        //WHEN
        UserProfileResponse response = apsUserService.getProfile(request.username());

        //THEN
        assertTrue(response.isEmailVerified());
        assertFalse(response.isDevicesExceeded());
        assertFalse(response.isMembershipActivated());
        assertEquals(1, response.getDevicesCount());
        assertNull(response.getMembership());
        assertFalse(response.isMembershipActivated());
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