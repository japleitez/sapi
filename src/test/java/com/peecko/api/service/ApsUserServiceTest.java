package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.domain.EntityDefault;
import com.peecko.api.domain.dto.DeviceDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.ApsDeviceRepo;
import com.peecko.api.repository.ApsMembershipRepo;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ApsUserServiceTest {

    @Autowired
    ApsMembershipRepo apsMembershipRepo;

    @Autowired
    ApsDeviceRepo apsDeviceRepo;

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    ApsUserService apsUserService;

    @Autowired
    PasswordEncoder passwordEncoder;

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
        assertNull(response.getMembership());
        assertTrue(response.isEmailVerified());
        assertFalse(response.isDevicesExceeded());
        assertFalse(response.isMembershipActivated());
        assertEquals(1, response.getDevicesCount());
    }

    @Test
    void signOut() {
        //GIVEN
        SignUpRequest signUp = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(signUp);

        //WHEN
        SignInRequest signIn = EntityBuilder.buildSignInRequest();
        UserProfileResponse response = apsUserService.signIn(signIn);
        //THEN
        assertEquals(1, response.getDevicesCount());

        //WHEN
        int deviceCount = apsUserService.signOut(signIn.username(), signIn.deviceId());
        ApsUser apsUser = apsUserRepo.findByUsernameWithDevices(signIn.username()).orElseThrow();

        //THEN
        assertEquals(0, deviceCount);
        assertEquals(0, apsUser.getApsDevices().size());

    }

    @Test
    void getUserDevicesAsDTO() {
        //GIVEN
        SignUpRequest signUp = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(signUp);

        SignInRequest signIn = EntityBuilder.buildSignInRequest();
        UserProfileResponse response = apsUserService.signIn(signIn);

        //WHEN
        List<DeviceDTO> devices = apsUserService.getUserDevicesAsDTO(signIn.username());

        //THEN
        assertEquals(1, devices.size());

    }

    @Test
    void updateUserPassword() {
        //GIVEN
        SignUpRequest signUp = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(signUp);

        SignInRequest signIn = EntityBuilder.buildSignInRequest();
        UserProfileResponse response = apsUserService.signIn(signIn);

        //WHEN
        String newPassword = "newPassword";
        apsUserService.updateUserPassword(signIn.username(), newPassword);

        //THEN
        ApsUser updated = apsUserRepo.findByUsername(signIn.username()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updated.getPassword()));
    }

    @Test
    void updateUserName() {
        //GIVEN
        SignUpRequest signUp = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(signUp);

        SignInRequest signIn = EntityBuilder.buildSignInRequest();
        UserProfileResponse response = apsUserService.signIn(signIn);

        //WHEN
        String otherName = "John Smith";
        apsUserService.updateUserName(signIn.username(), otherName);

        //THEN
        ApsUser updated = apsUserRepo.findByUsername(signIn.username()).orElseThrow();
        assertEquals(otherName, updated.getName());
    }

    @Test
    void passwordDoesNotMatch() {
        //GIVEN
        SignUpRequest signUp = EntityBuilder.buildSignUpRequest();
        apsUserService.signUp(signUp);

        //WHEN
        String wrongPassword = "wrongPassword";
        boolean doesNotMatch = apsUserService.passwordDoesNotMatch(signUp.username(), wrongPassword);

        //THEN
        assertTrue(doesNotMatch);
    }

}