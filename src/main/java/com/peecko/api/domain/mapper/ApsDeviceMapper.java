package com.peecko.api.domain.mapper;

import com.peecko.api.domain.ApsDevice;
import com.peecko.api.domain.dto.DeviceDTO;
import com.peecko.api.utils.Common;

public class ApsDeviceMapper {
    public static DeviceDTO deviceDTO(ApsDevice apsDevice) {
        DeviceDTO dto = new DeviceDTO();
        dto.setDeviceId(apsDevice.getDeviceId());
        dto.setOsVersion(apsDevice.getOsVersion());
        dto.setPhoneModel(apsDevice.getPhoneModel());
        dto.setInstalledOn(Common.instantAsString(apsDevice.getInstalledOn()));
        return dto;
    }
}
