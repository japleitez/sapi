package com.peecko.api.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter @Getter
public class PinValidationRequest {
    @JsonProperty("pin-code")
    String pinCode;
}
