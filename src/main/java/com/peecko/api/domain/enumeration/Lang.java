package com.peecko.api.domain.enumeration;

import org.springframework.util.StringUtils;

public enum Lang {
    EN,
    FR,
    DE,
    ES;

    public static Lang fromString(String langCode) {
        if (StringUtils.hasText(langCode)) {
            return EN;
        }
        try {
            return Lang.valueOf(langCode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EN;
        }
    }

}
