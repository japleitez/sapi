package com.peecko.api.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResetPasswordRequest(
        @JsonProperty("request-id") String requestId,
        @JsonProperty("pin-code") String pinCode,
        @JsonProperty("password") String password
) {
}
