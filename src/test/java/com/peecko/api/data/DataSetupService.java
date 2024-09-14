package com.peecko.api.data;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.Customer;
import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataSetupService {

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    CustomerRepo customerRepo;

    public void setup() {

        Customer customer = EntityBuilder.buildCustomer();
        customerRepo.save(customer);

        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUser.setActive(true);
        apsUserRepo.save(apsUser);
    }

}
