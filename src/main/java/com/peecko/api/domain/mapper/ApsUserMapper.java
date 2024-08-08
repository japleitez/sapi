package com.peecko.api.domain.mapper;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.dto.UserDTO;

public class ApsUserMapper {
    public static UserDTO toUserDTO(ApsUser apsUser) {
        if (apsUser == null) {
            return null;
        }
        UserDTO dto =  new UserDTO();
        dto.name(apsUser.getName());
        dto.username(apsUser.getUsername());
        dto.password(apsUser.getPassword());
        return dto;
    }
}
