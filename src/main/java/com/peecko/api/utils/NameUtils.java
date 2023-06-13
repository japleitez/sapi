package com.peecko.api.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//https://stackoverflow.com/questions/15805555/java-regex-to-validate-full-name-allow-only-spaces-and-letters
public abstract class NameUtils {
    private static String NAME_REGEX = "^\\p{L}+[\\p{L}\\p{Z}\\p{P}]{0,}";
    private static String INVALID_CHARS = "!#$%&()*+/0123456789:;<=>?@[\\]^_{|},~`";

    public static boolean isValid(String name) {
        if (!StringUtils.hasText(name)) {
            return false;
        }
        if (split(name).size() < 2) {
            return false;
        }
        boolean isInvalid = name.chars().anyMatch(ch -> isInvalidChar(ch));
        if (isInvalid) {
            return false;
        }
        Pattern p = Pattern.compile(NAME_REGEX);
        Matcher m = p.matcher(name);
        return m.matches();
    }

    private static boolean isInvalidChar(int ch) {
        return INVALID_CHARS.indexOf(ch) != -1;
    }

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

    public static String camel(String name) {
        List<String> list = split(name);
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for(String word: list) {
            String camel = "";
            if (word.length() > 2) {
                camel = Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase();
            } else {
                if (word.indexOf(".") > -1) {
                    camel = word.toUpperCase();
                } else {
                    camel = word.toLowerCase();
                }
            }
            if (i > 0) {
                builder.append(" ");
            }
            builder.append(camel);
            i++;
        }
        return builder.toString();
    }

}
