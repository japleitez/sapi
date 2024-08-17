package com.peecko.api.repository;

import com.peecko.api.domain.PlayList;
import com.peecko.api.domain.VideoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface VideoItemRepo extends JpaRepository<VideoItem, String> {

    Optional<VideoItem> findByCodeAAndPlaylist(String code, PlayList playList);

    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM VideoItem v WHERE v.code = :code AND v.playlist = :playList")
    boolean existsByCodeAndPlayList(@Param("code") String code, @Param("playList") PlayList playList);

}
