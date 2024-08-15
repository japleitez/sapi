package com.peecko.api.domain.mapper;

import com.peecko.api.domain.Language;
import com.peecko.api.domain.dto.LanguageDTO;

public class LanguageMapper {

    public static LanguageDTO languageDTO(Language language) {
        return new LanguageDTO(language.getCode(), language.getName());
    }
}
