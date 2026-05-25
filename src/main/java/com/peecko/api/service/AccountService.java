package com.peecko.api.service;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.repository.*;
import com.peecko.api.repository.TrialLicenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class AccountService {

    final ApsUserRepo apsUserRepo;
    final ApsMembershipRepo apsMembershipRepo;
    final TrialLicenseRepository trialLicenseRepository;

    public AccountService(ApsUserRepo apsUserRepo, ApsMembershipRepo apsMembershipRepo, TrialLicenseRepository trialLicenseRepository) {
        this.apsUserRepo = apsUserRepo;
        this.apsMembershipRepo = apsMembershipRepo;
        this.trialLicenseRepository = trialLicenseRepository;
    }

    @Transactional
    public boolean activateUserLicense(String username, Integer period, String license) {
        boolean activated = trialLicenseRepository.isNotExpired(license, LocalDate.now()) || apsMembershipRepo.existsByUsernameAndPeriodAndLicense(username, period, license);
        if (activated) {
            ApsUser apsUser = apsUserRepo.findByUsername(username).orElseThrow();
            apsUser.license(license);
            apsUser.active(true);
            apsUser.setUsernameVerified(true);
            apsUser.updated(Instant.now());
            apsUserRepo.save(apsUser);
        }
        return activated;
    }

}
