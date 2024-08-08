package com.peecko.api.repository;

import com.peecko.api.domain.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayListRepo extends JpaRepository<PlayList, Long> {
}
