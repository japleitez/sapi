package com.peecko.api.web.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter @Getter
public class SignUpRequest {
    String name;
    String username;
    String password;
    String language;
}
