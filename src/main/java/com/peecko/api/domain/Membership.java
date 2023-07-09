package com.peecko.api.domain;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Setter @Getter @Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Membership {
    @EqualsAndHashCode.Include
    String membership;
    String sponsor = "";
    String expire = "";
    String logo = "";
}
