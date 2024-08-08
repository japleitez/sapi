package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.domain.mapper.ApsUserMapper;
import com.peecko.api.repository.ApsUserRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApsUserService {
    private final ApsUserRepo apsUserRepo;

    public ApsUserService(ApsUserRepo apsUserRepo) {
        this.apsUserRepo = apsUserRepo;
    }

    public UserDTO findByUsernameOrElseThrow(String username) {
        ApsUser apsUser = apsUserRepo.findApsUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
        return ApsUserMapper.toUserDTO(apsUser);
    }

}
