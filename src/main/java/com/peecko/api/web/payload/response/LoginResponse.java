package com.peecko.api.web.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static com.peecko.api.utils.Common.MAX_ALLOWED;

@Data
@Setter @Getter
public class LoginResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String token;

    @JsonProperty("name")
    String name;

    @JsonProperty("username")
    String username;

    @JsonProperty("membership-number")
    String membership = "";

    @JsonProperty("membership-expire-date")
    String membershipExpireDate = "";

    @JsonProperty("membership-activated") //active-membership
    boolean membershipActivated = false;

    @JsonProperty("membership-sponsor")
    String membershipSponsor = "";

    @JsonProperty("membership-sponsor-logo")
    String membershipSponsorLogo = "";

    @JsonProperty("email-verified") //email-verified
    boolean emailVerified = true;

    @JsonProperty("devices-exceeded") // installations-exceeded
    boolean devicesExceeded = false;

    @JsonProperty("devices-count") // installations
    int devicesCount = 0;

    @JsonProperty("devices-max") // maxAllowed
    int devicesMax = MAX_ALLOWED;

}
