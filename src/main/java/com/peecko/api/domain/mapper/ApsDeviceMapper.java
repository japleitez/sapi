package com.peecko.api.domain.mapper;

import com.peecko.api.domain.ApsDevice;
import com.peecko.api.domain.dto.DeviceDTO;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.request.SignInRequest;

import java.time.Instant;

public class ApsDeviceMapper {

    public static DeviceDTO deviceDTO(ApsDevice apsDevice) {
        DeviceDTO dto = new DeviceDTO();
        dto.setDeviceId(apsDevice.getDeviceId());
        dto.setOsVersion(apsDevice.getOsVersion());
        dto.setPhoneModel(apsDevice.getPhoneModel());
        dto.setInstalledOn(Common.instantAsString(apsDevice.getInstalledOn()));
        return dto;
    }

    public static ApsDevice toApsDevice(SignInRequest request) {
        ApsDevice apsDevice = new ApsDevice();
        apsDevice.username(request.username());
        apsDevice.deviceId(request.deviceId());
        apsDevice.osVersion(request.osVersion());
        apsDevice.phoneModel(request.phoneModel());
        apsDevice.installedOn(Instant.now());
        return apsDevice;
    }

}
