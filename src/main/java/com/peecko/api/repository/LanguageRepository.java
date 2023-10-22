package com.peecko.api.repository;

import com.peecko.api.domain.Language;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class LanguageRepository {

    static final List<Language> LANGUAGES = new ArrayList<>();

    static {
        LANGUAGES.add(new Language("EN", "English"));
        LANGUAGES.add(new Language("FR", "French"));
        LANGUAGES.add(new Language("DE", "German"));
        LANGUAGES.add(new Language("ES", "Spanish"));
    }

    public List<Language> getLanguages() {
        return LANGUAGES;
    }
}
