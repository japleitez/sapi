package com.peecko.api.domain.mapper;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

public class ApsUserMapper {

    private ApsUserMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static UserDTO toUserDTO(ApsUser apsUser) {
        if (apsUser == null) {
            return null;
        }
        UserDTO dto =  new UserDTO();
        dto.name(apsUser.getName());
        dto.username(apsUser.getUsername());
        dto.password(apsUser.getPassword());
        dto.language(apsUser.getLanguage().name());
        return dto;
    }

    public static UserDetails toUserDetails(ApsUser apsUser) {
        if (apsUser == null) {
            return null;
        }
        return new UserDetailsImpl(apsUser.getName(), apsUser.getUsername(), apsUser.getPassword(), new ArrayList<>());
    }

}
