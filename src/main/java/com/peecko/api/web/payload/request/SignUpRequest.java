package com.peecko.api.web.payload.request;

public record SignUpRequest(
        String name,
        String username,
        String password,
        String language
) {
}
