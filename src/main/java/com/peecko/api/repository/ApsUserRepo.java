package com.peecko.api.repository;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface ApsUserRepo extends JpaRepository<ApsUser, Long> {

    Optional<ApsUser> findByUsername(String username);

    @Query("SELECT u FROM ApsUser u LEFT JOIN FETCH u.apsDevices WHERE u.username = :username")
    Optional<ApsUser> findByUsernameWithDevices(@Param("username") String username);

    boolean existsByUsername(String username);

}
