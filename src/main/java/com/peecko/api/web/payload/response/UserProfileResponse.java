package com.peecko.api.web.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.peecko.api.utils.Common.MAX_DEVICES_ALLOWED;

@Data
public class UserProfileResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String token;

    @JsonProperty("name")
    String name;

    @JsonProperty("username")
    String username;

    @JsonProperty("email-verified") //email-verified
    boolean emailVerified = true;

    @JsonProperty("devices-exceeded") // installations-exceeded
    boolean devicesExceeded = false;

    @JsonProperty("devices-count") // installations
    int devicesCount = 0;

    @JsonProperty("devices-max") // maxAllowed
    int devicesMax = MAX_DEVICES_ALLOWED;

    @JsonProperty("membership")
    String membership = "";

    @JsonProperty("membership-activated") //active-membership
    boolean membershipActivated = false;

    @JsonProperty("membership-expiration")
    String membershipExpiration = "";

    @JsonProperty("membership-sponsor")
    String membershipSponsor = "";

    @JsonProperty("membership-sponsor-logo")
    String membershipSponsorLogo = "";


    @Override
    public String toString() {
        return "UserProfileResponse{" +
                "token='" + token + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", emailVerified=" + emailVerified +
                ", devicesExceeded=" + devicesExceeded +
                ", devicesCount=" + devicesCount +
                ", devicesMax=" + devicesMax +
                ", membership='" + membership + '\'' +
                ", membershipActivated=" + membershipActivated +
                ", membershipExpiration='" + membershipExpiration + '\'' +
                ", membershipSponsor='" + membershipSponsor + '\'' +
                ", membershipSponsorLogo='" + membershipSponsorLogo + '\'' +
                '}';
    }
}
