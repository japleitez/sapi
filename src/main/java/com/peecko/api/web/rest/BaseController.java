package com.peecko.api.web.rest;

import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.repository.fake.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Locale;
import java.util.Set;

public abstract class BaseController {
    protected String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

}
