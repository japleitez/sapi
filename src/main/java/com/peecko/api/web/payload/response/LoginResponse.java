package com.peecko.api.web.payload.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
@Accessors(fluent = true)
@Getter
public class LoginResponse {
    private final Instant loginTs;
    private final String authToken;
    private final Duration tokenValidity;
    private final URL tokenRefreshUrl;
}
