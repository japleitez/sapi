package com.peecko.api.web.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peecko.api.domain.dto.DeviceDTO;

import java.util.List;

public record InstallationsResponse(
        @JsonProperty("max-allowed") int maxAllowed,
        @JsonProperty("installations") List<DeviceDTO> devices) {
}
