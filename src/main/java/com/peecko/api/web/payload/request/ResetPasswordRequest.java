package com.peecko.api.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter @Getter
public class ResetPasswordRequest {

    @JsonProperty("request-id")
    String requestId;

    @JsonProperty("pin-code")
    String pinCode;

    String password;
}
