package com.peecko.api.web.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peecko.api.domain.dto.LanguageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor @Getter
public class LanguageResponse {

    @JsonProperty("selected")
    private String selected;

    @JsonProperty("languages")
    private List<LanguageDTO> languageDTOS;

}
