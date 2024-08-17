package com.peecko.api.repository;

import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VideoRepo extends JpaRepository<Video, Long> {

    Optional<Video> findByCode(String code);

    @Query("SELECT v FROM Video v WHERE v.videoCategory IN :categories AND v.released IS NOT NULL ORDER BY v.released DESC")
    List<Video> findTopByCategoriesOrderByUploadDateDesc(@Param("categories") List<Long> categories, @Param("limit") int limit);

    @Query("SELECT v FROM Video v WHERE v.videoCategory = :videoCategory AND v.released <= :today AND (v.archived IS NULL OR v.archived > :today) ORDER BY v.released DESC")
    List<Video> findReleasedAndNotArchivedVideos(@Param("videoCategory") VideoCategory videoCategory, @Param("today") Instant today);

}
