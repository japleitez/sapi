package com.peecko.api.utils;

import java.util.regex.Pattern;

public class NameValidator {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 100;

    private static final String NAME_REGEX = "^[A-Za-zÁÉÍÓÚáéíóúüÑñ]+([ '-][A-Za-zÁÉÍÓÚáéíóúüÑñ]+)*$";

    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);

    public static boolean isNotValid(String name) {
        return !isValid(name);
    }

    public static boolean isValid(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(fullName).matches() && fullName.length() >= MIN_LENGTH && fullName.length() <= MAX_LENGTH;
    }

}

