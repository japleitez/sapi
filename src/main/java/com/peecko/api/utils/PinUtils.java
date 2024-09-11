package com.peecko.api.utils;

import java.util.Random;

public class PinUtils {

    private PinUtils() {
       throw new IllegalStateException("Utility class");
    }

    public static String randomDigitsAsString(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }

}
