package com.peecko.api.repository;

import com.peecko.api.domain.UserFavoriteVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserFavoriteVideoRepo extends JpaRepository<UserFavoriteVideo, Long> {
    List<UserFavoriteVideo> findByApsUserId(Long apsUserId);

    @Query("SELECT v.id FROM UserFavoriteVideo v WHERE v.apsUserId = :apsUserId")
    Set<Long> findVideoIdsByApsUserId(@Param("apsUserId") Long apsUserId);

}
