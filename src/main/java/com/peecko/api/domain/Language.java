package com.peecko.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Locale;

@Data
@AllArgsConstructor
@Setter @Getter
public class Language {
    private String code;
    private String name;
}
