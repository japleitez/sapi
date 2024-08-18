package com.peecko.api.security;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.repository.ApsMembershipRepo;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.InvalidJwtRepo;
import com.peecko.api.service.ApsUserService;
import com.peecko.api.utils.Common;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LicenseService {
    final JwtUtils jwtUtils;
    final InvalidJwtRepo invalidJwtRepo;
    final ApsUserRepo apsUserRepo;
    final ApsUserService apsUserService;
    final ApsMembershipRepo apsMembershipRepo;

    public LicenseService(JwtUtils jwtUtils, InvalidJwtRepo invalidJwtRepo, ApsUserRepo apsUserRepo, ApsUserService apsUserService, ApsMembershipRepo apsMembershipRepo) {
        this.jwtUtils = jwtUtils;
        this.invalidJwtRepo = invalidJwtRepo;
        this.apsUserRepo = apsUserRepo;
        this.apsUserService = apsUserService;
        this.apsMembershipRepo = apsMembershipRepo;
    }

    public boolean authorize() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        ApsUser apsUser = apsUserRepo.findByUsername(username).orElse(null);
        if (apsUser == null) {
            return false;
        }
        if (apsMembershipRepo
                .findByUsernameAndPeriodAndLicense(username, Common.currentPeriod(), apsUser.getLicense())
                .isPresent()) {
            // the user has a valid license in the current period
            return true;
        }
        //otherwise, we grant the user access for up to 10 days if he had a license in the previous period
        LocalDate today = LocalDate.now();
        if (today.getDayOfMonth() > 10) {
            return false;
        }
        LocalDate previousMonth = today.minusMonths(1);
        int previousYearMonth = previousMonth.getYear() * 100 + previousMonth.getMonthValue();
        return apsMembershipRepo
                .findByUsernameAndPeriod(username, previousYearMonth)
                .isPresent();
    }

}
