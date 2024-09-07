package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.EntityDefault;
import com.peecko.api.domain.PinCode;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.enumeration.Verification;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.PinCodeRepo;
import com.peecko.api.utils.PinUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.thymeleaf.TemplateEngine;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PinCodeServiceTest {

    @Autowired
    PinCodeRepo pinCodeRepo;

    @Autowired
    EmailService emailService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    ApsUserService apsUserService;

    @Autowired
    PinCodeService pinCodeService;

    @Test
    void generatePinCode() {
        // GIVEN

        // user
        ApsUser apsUser = new ApsUser();
        apsUser.setUsername("test@mail.com");
        apsUser.setName("Test");
        apsUser.setLanguage(Lang.FR);
        apsUserRepo.save(apsUser);
        apsUserRepo.flush();

        // WHEN
        String requestId = pinCodeService.generatePinCode(apsUser.getUsername(), Verification.RESET_PASSWORD);

        // THEN
        assertNotNull(requestId);
    }

    @Test
    void findByRequestId() {
        // GIVEN
        PinCode pinCode = new PinCode();
        pinCode.setLanguage(Lang.FR.name());
        pinCode.setEmail(EntityDefault.USERNAME);
        pinCode.setCode(PinUtils.randomDigitsAsString(4));
        pinCode.setExpireAt(LocalDateTime.now().plusMinutes(10));
        pinCode.setVerification(Verification.RESET_PASSWORD);
        pinCodeRepo.save(pinCode);

        String requestId = pinCode.getRequestId().toString();

        // WHEN
        PinCode result = pinCodeService.findByRequestId(requestId);

        // THEN
        assertNotNull(result);
        assertTrue(Objects.equals(result.getRequestId().toString(), requestId));
    }

    @Test
    void isPinCodeValid() {

        String code = PinUtils.randomDigitsAsString(4);

        // GIVEN
        PinCode pinCode = new PinCode();
        pinCode.setLanguage(Lang.FR.name());
        pinCode.setEmail(EntityDefault.USERNAME);
        pinCode.setCode(code);
        pinCode.setLanguage(Lang.FR.name());
        pinCode.setExpireAt(LocalDateTime.now().plusMinutes(10));
        pinCode.setVerification(Verification.RESET_PASSWORD);
        pinCodeRepo.save(pinCode);

        // WHEN
        boolean result = pinCodeService.isPinCodeValid(pinCode.getRequestId().toString(), code);

        // THEN
        assertTrue(result);
    }

}