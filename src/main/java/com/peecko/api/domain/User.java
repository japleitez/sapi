package com.peecko.api.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true, fluent = true)
public class User {
    private String name;
    private String username;
    private String password;
    private String license;
    private String language;
    private boolean verified = true;
    private Set<Role> roles = new HashSet<>();
}
