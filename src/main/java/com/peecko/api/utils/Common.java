package com.peecko.api.utils;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Common {

    private static final int MAX = 10;
    private static final int MIN = 1;

    private static final Random RANDOM = new Random();

    public static int getRandomNum() {
        return RANDOM.nextInt((MAX - MIN) + 1) + MIN;
    }

    public static void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
