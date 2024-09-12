package com.peecko.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DeviceDTO {
    @EqualsAndHashCode.Include
    @JsonProperty("device-id")
    String deviceId;
    @JsonProperty("phone-model")
    String phoneModel;
    @JsonProperty("os-version")
    String osVersion;
    @JsonProperty("installed-on")
    String installedOn;
    @JsonIgnore
    String jwt;
}
