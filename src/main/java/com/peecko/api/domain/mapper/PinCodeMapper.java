package com.peecko.api.domain.mapper;

import com.peecko.api.domain.PinCode;
import com.peecko.api.domain.dto.PinCodeDTO;

public class PinCodeMapper {
    public static PinCodeDTO pinCodeDTO(PinCode pinCode) {
        PinCodeDTO dto = new PinCodeDTO();
        dto.setEmail(pinCode.getEmail());
        dto.setPinCode(dto.getPinCode());
        dto.setRequestId(pinCode.getRequestId().toString());
        dto.setExpireAt(pinCode.getExpireAt());
        return dto;
    }
}
