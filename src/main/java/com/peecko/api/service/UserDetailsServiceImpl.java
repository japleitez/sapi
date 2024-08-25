package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.domain.mapper.UserDTOMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    final ApsUserService apsUserService;

    public UserDetailsServiceImpl(ApsUserService apsUserService) {
        this.apsUserService = apsUserService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userDTO = apsUserService.findByUsernameOrElseThrow(username);
        return UserDTOMapper.userDetails(userDTO);
    }

    public ApsUser findByUsername(String username) {
        return apsUserService.findByUsername(username);
    }

}
