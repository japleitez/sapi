package com.peecko.api.validator;

import java.util.UUID;

public class UuidValidator {

    public static boolean isNotValid(String input) {
        return !isValid(input);
    }

    public static boolean isValid(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
