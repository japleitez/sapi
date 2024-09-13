package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    final ApsUserRepo apsUserRepo;

    public UserDetailsServiceImpl(ApsUserRepo apsUserRepo) {
        this.apsUserRepo = apsUserRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ApsUser apsUser = apsUserRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new UserDetailsImpl(apsUser.getName(), apsUser.getUsername(), apsUser.getPassword(), new ArrayList<>());
    }

}
