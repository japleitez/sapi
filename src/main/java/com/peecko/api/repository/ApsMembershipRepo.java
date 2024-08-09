package com.peecko.api.repository;

import com.peecko.api.domain.ApsMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApsMembershipRepo extends JpaRepository {

    Optional<ApsMembership> findByUsernameAndPeriod(String username, Integer period);

}
