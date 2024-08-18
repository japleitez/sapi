package com.peecko.api.repository;

import com.peecko.api.domain.UserFavoriteVideo;
import com.peecko.api.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserFavoriteVideoRepo extends JpaRepository<UserFavoriteVideo, Long> {
    List<UserFavoriteVideo> findByApsUserId(Long apsUserId);

    @Query("SELECT v.id FROM UserFavoriteVideo v WHERE v.apsUserId = :apsUserId")
    Set<Long> findVideoIdsByApsUserId(@Param("apsUserId") Long apsUserId);

    List<UserFavoriteVideo> findByApsUserIdOrderByIdDesc(Long apsUserId);


    @Transactional
    void deleteByApsUserId(Long apsUserId);

    @Transactional
    void deleteByApsUserIdAndVideo(Long apsUserId, Video video);

}
