package com.peecko.api.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class NameUtils {

    public static List<String> split(String name) {
        if (!StringUtils.hasText(name)) {
            return new ArrayList<>();
        }
        return Arrays.asList(name.trim().split("\\s*( \\s*)+"));
    }

    public static String trim(String name) {
        if (!StringUtils.hasText(name)) {
            return "";
        }
        return String.join(" ", split(name));
    }

    public static String toCamelCase(String input) {
        String[] words = input.split(" ");
        StringBuilder camelCaseName = new StringBuilder();
        for (String word : words) {
            word = word.trim().toLowerCase();
            camelCaseName
                    .append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return camelCaseName.toString().trim();
    }

}
