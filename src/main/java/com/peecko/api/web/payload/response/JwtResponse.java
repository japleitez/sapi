package com.peecko.api.web.payload.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter @Getter
public class JwtResponse {
    String token;
    String name;
    String username;
    List<String> roles;
}
