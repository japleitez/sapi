package com.peecko.api.service;

import com.peecko.api.domain.dto.Help;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.mapper.HelpItemMapper;
import com.peecko.api.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {

    final ApsUserRepo apsUserRepo;
    final ApsMembershipRepo apsMembershipRepo;

    public AccountService(ApsUserRepo apsUserRepo, ApsMembershipRepo apsMembershipRepo) {
        this.apsUserRepo = apsUserRepo;
        this.apsMembershipRepo = apsMembershipRepo;
    }

    public boolean activateUserLicense(String username, Integer period, String license) {
        boolean activated = apsMembershipRepo.existsByUsernameAndPeriodAndLicense(username, period, license);
        if (activated) {
            apsUserRepo.setLicense(username, license);
        }
        return activated;
    }

}
