package com.peecko.api.web.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProfileResponse {
    @JsonProperty("active-membership")
    private boolean activeMembership;
    @JsonProperty("email-verified")
    private boolean emailVerified;
    @JsonProperty("installations-exceeded")
    private boolean installationsExceeded;
    @JsonProperty("installations")
    private int installations;
    @JsonProperty("maxAllowed")
    private int maxAllowed;
}
