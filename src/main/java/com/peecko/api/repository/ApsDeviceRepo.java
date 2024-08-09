package com.peecko.api.repository;

import com.peecko.api.domain.ApsDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApsDeviceRepo extends JpaRepository<ApsDevice, Long> {

    Optional<ApsDevice> findByUsernameAndDeviceId(String username, String deviceId);
}
