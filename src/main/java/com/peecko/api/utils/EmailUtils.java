package com.peecko.api.utils;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class EmailUtils {

    private static final String REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
        + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final String OWASP = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static String INVALID_CHARS = "!#$%&()*+/:;<=>?[\\]^{|},~`";

    public static boolean isValid(String email) {
        if (!StringUtils.hasText(email)) {
            return false;
        }
        email = email.trim();
        boolean isInvalid = email.chars().anyMatch(ch -> isInvalidChar(ch));
        if (isInvalid) {
            return false;
        }
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(email);

        Pattern op = Pattern.compile(OWASP);
        Matcher om = op.matcher(email);

        return m.matches() && om.matches();
    }

    private static boolean isInvalidChar(int ch) {
        return INVALID_CHARS.indexOf(ch) != -1;
    }

}
