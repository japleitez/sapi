package com.peecko.api.web.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peecko.api.domain.Device;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter @Getter
public class JwtResponse {

    @JsonProperty("token")
    String token;

    @JsonProperty("name")
    String name;

    @JsonProperty("username")
    String username;

    @JsonProperty("roles")
    List<String> roles;

    @JsonProperty("max-allowed")
    int maxAllowed;

    @JsonProperty("installations")
    List<Device> installations;

    @JsonProperty("installations-count")
    Integer installationsCount;

    @JsonProperty("membership-status")
    String membershipStatus;

    @JsonProperty("membership-message")
    String membershipMessage;

    @JsonProperty("account-status")
    String accountStatus;

    @JsonProperty("account-message")
    String accountMessage;

}
