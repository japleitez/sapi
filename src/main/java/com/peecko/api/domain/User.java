package com.peecko.api.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true, fluent = true)
public class User {
    private Long id;
    private String name;
    private String surname;
    private String email;
    private String password;
    private String license;
}
