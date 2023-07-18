package com.peecko.api.domain;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Setter
@Getter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Coach {
    @EqualsAndHashCode.Include
    private String name;
    private String website;
    private String email;
    private String instagram;
}
