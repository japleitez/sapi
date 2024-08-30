package com.peecko.api.web.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peecko.api.domain.dto.LanguageDTO;

import java.util.List;

public record LanguageResponse(
        @JsonProperty("selected") String selected,
        @JsonProperty("languages") List<LanguageDTO> languageDTOS
) { }
