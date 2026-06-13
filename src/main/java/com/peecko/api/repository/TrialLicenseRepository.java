package com.peecko.api.repository;

import com.peecko.api.domain.TrialLicense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TrialLicenseRepository extends JpaRepository<TrialLicense, String> {

    @Query("SELECT COUNT(t) > 0 FROM TrialLicense t WHERE t.license = :license AND t.expirationDate >= :today")
    boolean isNotExpired(@Param("license") String license, @Param("today") LocalDate today);

}
