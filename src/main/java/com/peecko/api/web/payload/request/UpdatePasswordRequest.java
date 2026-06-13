package com.peecko.api.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdatePasswordRequest(
        @JsonProperty("username") String username,
        @JsonProperty("old-password") String current,
        @JsonProperty("new-password") String password
) {
}
