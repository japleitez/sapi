package com.peecko.api.utils;

import org.springframework.util.StringUtils;

public abstract class PasswordUtils {

    static int MIN_LENGTH = 6;
    static String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    static String SYMBOL = "!@#$%&*+";
    static String DIGIT = "0123456789";

    public static boolean isValid(String password) {
        if (!StringUtils.hasText(password)) {
            return false;
        }
        password = password.trim();
        if (password.length() < MIN_LENGTH) {
            return false;
        }
        boolean isInvalid = password.chars().anyMatch(ch -> isInvalidChar(ch));
        if (isInvalid) {
            return false;
        }
        return true;
    }

    private static boolean isInvalidChar(int ch) {
        return !isValidChar(ch);
    }

    private static boolean isValidChar(int ch) {
        return isUpperCase(ch) || isLowerCase(ch) || isSymbol(ch) || isDigit(ch);
    }
    private static boolean isUpperCase(int ch) {
        return UPPER_CASE.indexOf(ch) > -1;
    }

    private static boolean isLowerCase(int ch) {
        return LOWER_CASE.indexOf(ch) > -1;
    }

    private static boolean isSymbol(int ch) {
        return SYMBOL.indexOf(ch) > -1;
    }

    private static boolean isDigit(int ch) {
        return DIGIT.indexOf(ch) > -1;
    }

}
