package com.peecko.api.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PinValidationRequest(@JsonProperty("pin-code") String code) {
}
