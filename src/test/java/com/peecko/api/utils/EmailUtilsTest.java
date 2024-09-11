package com.peecko.api.utils;

import com.peecko.api.validator.EmailValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailUtilsTest {

    @Test
    void validEmails() {
        assertTrue(EmailValidator.isValid("username@domain.com"));
        assertTrue(EmailValidator.isValid("user.name@domain.com"));
        assertTrue(EmailValidator.isValid("user-name@domain.com"));
        assertTrue(EmailValidator.isValid("username@domain.co.in"));
        assertTrue(EmailValidator.isValid("user_name@domain.com"));
        assertTrue(EmailValidator.isValid("user.name@arhs-cube.com"));
        assertTrue(EmailValidator.isValid("user@example.com"));
        assertTrue(EmailValidator.isValid("user.name@example.co"));
        assertTrue(EmailValidator.isValid("user_name@example.com"));
        assertTrue(EmailValidator.isValid("user@sub.example.com"));

    }

    @Test
    void invalidEmails() {
        assertFalse(EmailValidator.isValid("username.@domain.com"));
        assertFalse(EmailValidator.isValid(".user.name@domain.com"));
        assertFalse(EmailValidator.isValid("user-name@domain.com."));
        assertFalse(EmailValidator.isValid("username@.com"));
        assertFalse(EmailValidator.isValid("user%name@domain.com"));
        assertFalse(EmailValidator.isValid("user$name@domain.com"));
        assertFalse(EmailValidator.isValid("user@com"));
        assertFalse(EmailValidator.isValid("user@.com"));
        assertFalse(EmailValidator.isValid("user@com."));
        assertFalse(EmailValidator.isValid("@example.com"));
        assertFalse(EmailValidator.isValid("user@.example.com"));

    }

}
