package com.peecko.api.utils;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class NameUtils {

    private NameUtils() {
       throw new IllegalStateException("Utility class");
    }

    public static String toCamelCase(String input) {
        if (!StringUtils.hasText(input)) {
           return "";
        }
        String[] words = input.trim().split("\\s+");
        String camelCase = Arrays.stream(words)
              .map(String::toLowerCase)
              .map(NameUtils::capitalize)
              .collect(Collectors.joining());
        return camelCase.trim();
    }

    private static String capitalize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase() + " ";
    }

}
