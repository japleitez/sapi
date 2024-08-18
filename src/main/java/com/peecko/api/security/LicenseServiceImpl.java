package com.peecko.api.security;

import com.peecko.api.domain.dto.Role;
import com.peecko.api.domain.dto.UserDTO;
import com.peecko.api.repository.InvalidJwtRepo;
import com.peecko.api.service.ApsUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class LicenseServiceImpl {
    public static final HashMap<String, UserDTO> REPO = new HashMap<>();
    public static final Set<String> INVALID_JWT = new HashSet<>();
    public static final Set<String> INVALID_LICENSE = new HashSet<>();
    public static final Set<Role> DEFAULT_ROLES = new HashSet<>();

    static {
        DEFAULT_ROLES.add(Role.USER);
    }

    final JwtUtils jwtUtils;
    final InvalidJwtRepo invalidJwtRepo;
    final ApsUserService apsUserService;

    public LicenseServiceImpl(JwtUtils jwtUtils, InvalidJwtRepo invalidJwtRepo, ApsUserService apsUserService) {
        this.jwtUtils = jwtUtils;
        this.invalidJwtRepo = invalidJwtRepo;
        this.apsUserService = apsUserService;
    }

    public boolean authorize() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return hasActiveLicense(userDetails.getUsername());
    }
    boolean isValidLicense(String license) {
        return StringUtils.hasLength(license) && license.length() == 20 && license.startsWith("1111") && !INVALID_LICENSE.contains(license);
    }

    boolean hasActiveLicense(String username) {
        //TODO implementation
        if (REPO.containsKey(username)) {
            UserDTO userDTO = REPO.get(username);
            return isValidLicense(userDTO.license());
        }
        return false;
    }

}
