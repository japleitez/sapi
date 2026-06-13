package com.peecko.api.web.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PinCodeResponse(@JsonProperty("request-id") String requestId) {
}
