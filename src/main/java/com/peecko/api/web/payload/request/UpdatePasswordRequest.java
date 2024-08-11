package com.peecko.api.web.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter @Getter
public class UpdatePasswordRequest {

    String username;

    @JsonProperty("old-password")
    String current;

    @JsonProperty("new-password")
    String password;

}
