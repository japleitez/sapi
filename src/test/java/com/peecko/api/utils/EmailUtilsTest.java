package com.peecko.api.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailUtilsTest {

    @Test
    public void isValid() {
        assertTrue(EmailUtils.isValid("username@domain.com"));
        assertTrue(EmailUtils.isValid("user.name@domain.com"));
        assertTrue(EmailUtils.isValid("user-name@domain.com"));
        assertTrue(EmailUtils.isValid("username@domain.co.in"));
        assertTrue(EmailUtils.isValid("user_name@domain.com"));
        assertTrue(EmailUtils.isValid("user.name@arhs-cube.com"));
    }

    @Test void isInvalid() {
        assertFalse(EmailUtils.isValid("username.@domain.com"));
        assertFalse(EmailUtils.isValid(".user.name@domain.com"));
        assertFalse(EmailUtils.isValid("user-name@domain.com."));
        assertFalse(EmailUtils.isValid("username@.com"));
        assertFalse(EmailUtils.isValid("user%name@domain.com"));
        assertFalse(EmailUtils.isValid("user$name@domain.com"));
    }
}
