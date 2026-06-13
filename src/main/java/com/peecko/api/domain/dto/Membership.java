package com.peecko.api.domain.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Membership {
    @EqualsAndHashCode.Include
    String license = "";
    String sponsor = "";
    String expiration = "";
    String logo = "";
}
