package com.peecko.api.service;

import com.peecko.api.domain.Language;
import com.peecko.api.domain.dto.LanguageDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.LanguageRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LanguageServiceTest {

    @Autowired
    LanguageService languageService;

    @Autowired
    LanguageRepo languageRepo;

    @Test
    void findActiveLanguages() {
        // given
        languageRepo.save(new Language(Lang.EN.name(), "English", true));
        languageRepo.save(new Language(Lang.FR.name(), "French", true));
        languageRepo.save(new Language(Lang.DE.name(), "Deutsch", true));
        languageRepo.save(new Language(Lang.ES.name(), "Español", false));
        languageRepo.flush();

        // when
        List<LanguageDTO> languages = languageService.findActiveLanguages();

        // then
        assertEquals(3, languages.size());
        assertEquals(languages.stream().filter(l -> l.getName().equals("English")).count(), 1);
        assertEquals(languages.stream().filter(l -> l.getName().equals("French")).count(), 1);
        assertEquals(languages.stream().filter(l -> l.getName().equals("Deutsch")).count(), 1);
    }



}