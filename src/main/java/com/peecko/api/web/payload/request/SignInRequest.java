package com.peecko.api.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SignInRequest(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("phone-model") String phoneModel,
        @JsonProperty("os-version") String osVersion,
        @JsonProperty("device-id") String deviceId
) { }
