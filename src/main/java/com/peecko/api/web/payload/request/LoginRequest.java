package com.peecko.api.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter @Getter
public class LoginRequest {
    String username;
    String password;
    @JsonProperty("phone-model")
    String phoneModel;
    @JsonProperty("os-version")
    String osVersion;
    @JsonProperty("device-id")
    String deviceId;
}
