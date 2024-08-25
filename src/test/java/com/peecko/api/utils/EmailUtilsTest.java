package com.peecko.api.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailUtilsTest {

    @Test
    public void validEmails() {
        assertTrue(EmailValidator.isValidEmail("username@domain.com"));
        assertTrue(EmailValidator.isValidEmail("user.name@domain.com"));
        assertTrue(EmailValidator.isValidEmail("user-name@domain.com"));
        assertTrue(EmailValidator.isValidEmail("username@domain.co.in"));
        assertTrue(EmailValidator.isValidEmail("user_name@domain.com"));
        assertTrue(EmailValidator.isValidEmail("user.name@arhs-cube.com"));

        assertTrue(EmailValidator.isValidEmail("user@example.com"));
        assertTrue(EmailValidator.isValidEmail("user.name@example.co"));
        assertTrue(EmailValidator.isValidEmail("user_name@example.com"));
        assertTrue(EmailValidator.isValidEmail("user@sub.example.com"));

    }

    @Test
    public void invalidEmails() {
        assertFalse(EmailValidator.isValidEmail("username.@domain.com"));
        assertFalse(EmailValidator.isValidEmail(".user.name@domain.com"));
        assertFalse(EmailValidator.isValidEmail("user-name@domain.com."));
        assertFalse(EmailValidator.isValidEmail("username@.com"));
        assertFalse(EmailValidator.isValidEmail("user%name@domain.com"));
        assertFalse(EmailValidator.isValidEmail("user$name@domain.com"));

        assertFalse(EmailValidator.isValidEmail("user@com"));
        assertFalse(EmailValidator.isValidEmail("user@.com"));
        assertFalse(EmailValidator.isValidEmail("user@com."));
        assertFalse(EmailValidator.isValidEmail("@example.com"));
        assertFalse(EmailValidator.isValidEmail("user@.example.com"));

    }

}
