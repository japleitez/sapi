package com.peecko.api.repository;

import com.peecko.api.domain.ApsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApsUserRepo extends JpaRepository<ApsUser, Long> {
    Optional<ApsUser> findApsUserByUsername(String username);

}
