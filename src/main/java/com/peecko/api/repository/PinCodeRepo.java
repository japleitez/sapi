package com.peecko.api.repository;

import com.peecko.api.domain.PinCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PinCodeRepo extends JpaRepository<PinCode, UUID> {
}
