package com.peecko.api.service;

import com.peecko.api.domain.ApsDevice;
import com.peecko.api.repository.ApsDeviceRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ApsDeviceService {

    private final ApsDeviceRepo apsDeviceRepo;

    public ApsDeviceService(ApsDeviceRepo apsDeviceRepo) {
        this.apsDeviceRepo = apsDeviceRepo;
    }

    @Transactional
    public void deleteDevice(String username, String deviceId) {
        apsDeviceRepo.findByUsernameAndDeviceId(username, deviceId).ifPresent(apsDeviceRepo::delete);
    }

    public void saveDevice(ApsDevice apsDevice) {
        apsDeviceRepo.save(apsDevice);
    }

}
