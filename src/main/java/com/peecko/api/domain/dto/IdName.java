package com.peecko.api.domain.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdName {
    Long id;
    String name;
    Integer counter;
}
