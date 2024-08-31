package com.peecko.api.service;

import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.domain.mapper.ApsUserMapper;
import com.peecko.api.domain.mapper.UserDTOMapper;
import com.peecko.api.repository.ApsUserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    final ApsUserRepo apsUserRepo;

    public UserDetailsServiceImpl(ApsUserRepo apsUserRepo) {
        this.apsUserRepo = apsUserRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDTO userDTO = apsUserRepo.findByUsername(username)
                .map(ApsUserMapper::toUserDTO)
                .orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
        return UserDTOMapper.userDetails(userDTO);
    }

}
