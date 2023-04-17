package com.peecko.api.web.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter @Getter
public class SignupRequest {
    String name;
    String username;
    String password;
}
