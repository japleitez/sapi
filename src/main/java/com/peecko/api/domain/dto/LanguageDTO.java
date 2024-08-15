package com.peecko.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Setter @Getter
public class LanguageDTO {
    private String code;
    private String name;
}
