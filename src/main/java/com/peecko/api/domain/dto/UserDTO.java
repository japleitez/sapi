package com.peecko.api.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true, fluent = true)
public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String password;
    private String license;
    private String language;
    private boolean verified = true;
    private Membership membership;
    private Set<Role> roles = new HashSet<>();
}
