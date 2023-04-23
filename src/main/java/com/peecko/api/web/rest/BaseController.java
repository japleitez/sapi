package com.peecko.api.web.rest;

import com.peecko.api.domain.User;
import com.peecko.api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class BaseController {

    protected String getActiveLanguage(UserRepository userRepository) {
        User user = getActiveUser(userRepository);
        return user != null? user.language(): "EN";
    }
    protected User getActiveUser(UserRepository userRepository) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername()).get();
    }

}
