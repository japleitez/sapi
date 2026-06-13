package com.peecko.api.utils;

import java.util.Random;

public class PinUtils {
   private static final Random RANDOM = new Random();

    private PinUtils() {
       throw new IllegalStateException("Utility class");
    }

    public static String randomDigitsAsString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int digit = RANDOM.nextInt(10);
            stringBuilder.append(digit);
        }
        return stringBuilder.toString();
    }

}
