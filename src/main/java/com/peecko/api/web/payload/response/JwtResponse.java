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
    String token;
    String name;
    String username;
    List<String> roles;
    @JsonProperty("max-allowed")
    int maxAllowed;
    List<Device> installations;
}
