package com.peecko.api.domain.enumeration;

public enum Lang {
    EN,
    FR,
    DE,
    ES;

    public static Lang fromString(String langCode) {
        try {
            return Lang.valueOf(langCode.toUpperCase());
        } catch (Exception e) {
            return EN;
        }
    }

}
