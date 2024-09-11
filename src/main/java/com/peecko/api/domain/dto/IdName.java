package com.peecko.api.domain.dto;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdName {
    Long id;
    String name;
    Integer counter;
}
