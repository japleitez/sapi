package com.peecko.api.repository.fake;

import com.peecko.api.domain.dto.LanguageDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LanguageRepository {

    static final List<LanguageDTO> LANGUAGE_DTOS = new ArrayList<>();

    static {
        LANGUAGE_DTOS.add(new LanguageDTO("EN", "English"));
        LANGUAGE_DTOS.add(new LanguageDTO("FR", "French"));
        LANGUAGE_DTOS.add(new LanguageDTO("DE", "German"));
        LANGUAGE_DTOS.add(new LanguageDTO("ES", "Spanish"));
    }

    public List<LanguageDTO> getLanguages() {
        return LANGUAGE_DTOS;
    }
}
