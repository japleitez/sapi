package com.peecko.api.web.rest;

import com.peecko.api.domain.Language;
import com.peecko.api.domain.User;
import com.peecko.api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public abstract class BaseController {

    private static Set<String> availableLanguages = Set.of("EN", "FR", "DE", "ES");

    protected String getActiveLanguage(UserRepository userRepository) {
        User user = getActiveUser(userRepository);
        return user != null? user.language(): "en";
    }

    protected String getUsername(UserRepository userRepository) {
        return getActiveUser(userRepository).username();
    }

    protected User getActiveUser(UserRepository userRepository) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername()).get();
    }

    protected Locale geActiveLocale(UserRepository userRepository) {
        User user =  null;
        try {
            user = getActiveUser(userRepository);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String lang = user != null? user.language(): "en";
        Locale locale = Locale.forLanguageTag(lang);
        return locale;
    }

    protected String resolveLanguage(String lang) {
        String found = "EN";
        if (StringUtils.hasText(lang)) {
            if (!availableLanguages.contains(lang)) {
                found = "EN";
            }
        }
        return found;
    }
}
