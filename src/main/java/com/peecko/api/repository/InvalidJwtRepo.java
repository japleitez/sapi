package com.peecko.api.repository;

import com.peecko.api.domain.InvalidJwt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InvalidJwtRepo extends JpaRepository<InvalidJwt, Long> {

    Optional<InvalidJwt> findByJti(String jti);

}
