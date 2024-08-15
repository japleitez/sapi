package com.peecko.api.utils;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LangDTOTest {

    @Test
    public void LocaleTest() {
        Locale actual = Locale.forLanguageTag("es");
        assertEquals(actual.getLanguage(),"es");
    }
}
