package com.peecko.api.repository;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlayListRepo extends JpaRepository<PlayList, Long> {

    List<PlayList> findByApsUser(ApsUser apsUser);

    Optional<PlayList> findByApsUserAndName(ApsUser apsUser, String name);

    boolean existsById(Long id);

    Optional<PlayList> findById(Long id);

    @Query("SELECT DISTINCT p FROM PlayList p LEFT JOIN FETCH p.videoItems WHERE p.id = :id")
    Optional<PlayList> findByIdWithVideoItems(@Param("id") Long id);

}
