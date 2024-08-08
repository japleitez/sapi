package com.peecko.api.security;

import com.peecko.api.repository.fake.UserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class LicenseServiceImpl {

    final UserRepository userRepository;

    public LicenseServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authorize() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.hasActiveLicense(userDetails.getUsername());
    }

}
