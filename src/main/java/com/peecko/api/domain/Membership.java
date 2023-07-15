package com.peecko.api.domain;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@Setter @Getter @Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Membership {
    @EqualsAndHashCode.Include
    String license = "";
    String sponsor = "";
    String expiration = "";
    String logo = "";
}
