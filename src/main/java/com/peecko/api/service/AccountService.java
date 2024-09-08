package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    final ApsUserRepo apsUserRepo;
    final ApsMembershipRepo apsMembershipRepo;

    public AccountService(ApsUserRepo apsUserRepo, ApsMembershipRepo apsMembershipRepo) {
        this.apsUserRepo = apsUserRepo;
        this.apsMembershipRepo = apsMembershipRepo;
    }

    @Transactional
    public boolean activateUserLicense(String username, Integer period, String license) {
        boolean activated = apsMembershipRepo.existsByUsernameAndPeriodAndLicense(username, period, license);
        if (activated) {
            ApsUser apsUser = apsUserRepo.findByUsername(username).orElseThrow();
            apsUser.license(license);
            apsUserRepo.save(apsUser);
        }
        return activated;
    }

}
