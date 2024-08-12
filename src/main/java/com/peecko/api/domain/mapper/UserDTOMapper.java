package com.peecko.api.domain.mapper;

import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.security.UserDetailsImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

public class UserDTOMapper {

    public static UserDetails userDetails(UserDTO userDTO) {
        List<GrantedAuthority> authorities = userDTO
                .roles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(userDTO.name(), userDTO.username(), userDTO.password(), authorities);
    }

}
