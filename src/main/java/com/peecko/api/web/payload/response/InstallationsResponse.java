package com.peecko.api.web.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.peecko.api.domain.dto.DeviceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
public class InstallationsResponse {
    @JsonProperty("max-allowed")
    int maxAllowed;
    @JsonProperty("installations")
    List<DeviceDTO> devices;
}
