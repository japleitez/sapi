package com.peecko.api.web.rest;

import com.peecko.api.domain.dto.User;
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
        User user = getActiveUser(userRepository);
        return user != null? user.language(): EN;
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
        String lang = user != null? resolveLanguage(user.language()): EN;
        Locale locale = Locale.forLanguageTag(lang);
        return locale;
    }

    protected String resolveLanguage(String lang) {
        return availableLanguages.contains(lang)? lang.toUpperCase(): EN;
    }

}
