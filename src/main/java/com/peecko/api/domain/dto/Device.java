package com.peecko.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Setter
@Getter
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Device {
    @EqualsAndHashCode.Include
    @JsonProperty("device-id")
    String deviceId;
    @JsonProperty("phone-model")
    String phoneModel;
    @JsonProperty("os-version")
    String osVersion;
    @JsonProperty("installed-on")
    String installed;
    @JsonIgnore
    String jwt;
}
