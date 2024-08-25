package com.peecko.api.domain.mapper;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.security.UserDetailsImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ApsUserMapper {
    public static UserDTO userDTO(ApsUser apsUser) {
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

    public static UserDetails userDetails(ApsUser apsUser) {
        if (apsUser == null) {
            return null;
        }
        return new UserDetailsImpl(apsUser.getName(), apsUser.getUsername(), apsUser.getPassword(), new ArrayList<>());
    }

}
