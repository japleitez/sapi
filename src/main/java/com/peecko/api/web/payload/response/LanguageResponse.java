package com.peecko.api.web.payload.response;

import com.peecko.api.domain.dto.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor @Getter
public class LanguageResponse {
    private String selected;
    private List<Language> languages;
}
