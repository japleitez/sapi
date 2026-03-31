package com.peecko.api.repository;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.PlayList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query("UPDATE PlayList p SET p.counter = :counter WHERE p.id = :id")
    int updateCounter(@Param("id") Long id, @Param("counter") int counter);

}
