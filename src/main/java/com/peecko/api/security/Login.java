package com.peecko.api.security;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.enumeration.Lang;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

public class Login {

    public final static String CURRENT_USER = "currentUser";

    private final static ApsUser UNDEFINED_APS_USER = new ApsUser(0L, Lang.EN);

    public static ApsUser getUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        ApsUser apsUser = (ApsUser) request.getAttribute(CURRENT_USER);
        return apsUser != null? apsUser : UNDEFINED_APS_USER;
    }

    public static Long getUserId() {
        return getUser().getId();
    }

    public static Locale getLocale() {
        return getUser().getLocale();
    }

}
