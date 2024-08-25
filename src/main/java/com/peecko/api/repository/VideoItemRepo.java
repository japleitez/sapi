package com.peecko.api.repository;

import com.peecko.api.domain.VideoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface VideoItemRepo extends JpaRepository<VideoItem, String> {

    @Query("SELECT v FROM VideoItem v WHERE v.playList.id = :playlistId AND v.code = :code")
    Optional<VideoItem> findByPlayListIdAndVideoCode(@Param("playlistId") Long playlistId, @Param("code") String code);

    @Query("SELECT COUNT(v) > 0 FROM VideoItem v WHERE v.playList.id = :playListId AND v.code = :code")
    boolean existsByPlayListIdAndVideoCode(@Param("playListId") Long playListId, @Param("code") String code);

}
