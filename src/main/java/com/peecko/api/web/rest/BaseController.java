package com.peecko.api.web.rest;

import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.repository.fake.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Locale;
import java.util.Set;

public abstract class BaseController {

    public static String EN = "EN";
    public static String DE = "DE";
    public static String FR = "FR";
    public static String ES = "ES";

    private static Set<String> availableLanguages = Set.of(EN, FR, DE, ES);

    protected String getActiveLanguage(UserRepository userRepository) {
        UserDTO userDTO = getActiveUser(userRepository);
        return userDTO != null? userDTO.language(): EN;
    }

    protected String getUsername(UserRepository userRepository) {
        return getActiveUser(userRepository).username();
    }

    protected UserDTO getActiveUser(UserRepository userRepository) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername()).get();
    }

    protected Locale geActiveLocale(UserRepository userRepository) {
        UserDTO userDTO =  null;
        try {
            userDTO = getActiveUser(userRepository);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String lang = userDTO != null? resolveLanguage(userDTO.language()): EN;
        Locale locale = Locale.forLanguageTag(lang);
        return locale;
    }

    protected String resolveLanguage(String lang) {
        return availableLanguages.contains(lang)? lang.toUpperCase(): EN;
    }

}
