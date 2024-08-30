package com.peecko.api.utils;

public abstract class NameUtils {

    public static String toCamelCase(String input) {
        String[] words = input.split(" ");
        StringBuilder camelcase = new StringBuilder();
        for (String word : words) {
            word = word.trim().toLowerCase();
            camelcase
                    .append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1))
                    .append(" ");
        }
        return camelcase.toString().trim();
    }

}
