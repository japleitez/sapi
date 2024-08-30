package com.peecko.api.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignOutRequest(
        @JsonProperty("username") String username,
        @JsonProperty("device-id") String deviceId
) {
}
