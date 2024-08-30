package com.peecko.api.validator;

import java.util.regex.Pattern;

public abstract class PasswordValidator {

    private static final String VALID_SYMBOLS_REGEX = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\",.<>/?]+$";

    private static final int MIN_PASSWORD_LENGTH = 6;

    public static boolean isNotValid(String password) {
        return !isValid(password);
    }

    public static boolean isValid(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        return Pattern.matches(VALID_SYMBOLS_REGEX, password);
    }

}
