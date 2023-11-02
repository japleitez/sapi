package com.peecko.api.domain;


import lombok.*;
import lombok.experimental.Accessors;

@Data
@Setter
@Getter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class IdName {
    Long id;
    String name;
    Integer counter;
}
