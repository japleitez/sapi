package com.peecko.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Setter @Getter @Accessors(chain = true)
public class Help {
    private String question;
    private String answer;
}
