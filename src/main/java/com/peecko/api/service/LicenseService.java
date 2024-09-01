package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.repository.ApsMembershipRepo;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.utils.Common;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LicenseService {
    final ApsUserRepo apsUserRepo;
    final ApsMembershipRepo apsMembershipRepo;
    final int GRACE_PERIOD_IN_DAYS = 10;

    public LicenseService(ApsUserRepo apsUserRepo, ApsMembershipRepo apsMembershipRepo) {
        this.apsUserRepo = apsUserRepo;
        this.apsMembershipRepo = apsMembershipRepo;
    }

    public boolean isAuthorized() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return isAuthorized(userDetails.getUsername());
    }

    protected boolean isAuthorized(String username) {
        ApsUser apsUser = apsUserRepo.findByUsername(username).orElse(null);
        if (apsUser == null) {
            return false;
        }
        // if the user has a valid license in the current period, we return true
        if (apsMembershipRepo
                .findByUsernameAndPeriodAndLicense(username, Common.currentPeriod(), apsUser.getLicense())
                .isPresent()) {
            return true;
        }
        // otherwise, grant grace period if the user had any license in the previous period
        return GRACE_PERIOD_IN_DAYS > LocalDate.now().getDayOfMonth() &&
                apsMembershipRepo
                        .findByUsernameAndPeriod(username, Common.previousPeriod())
                        .isPresent();
    }

}
