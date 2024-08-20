package com.peecko.api.repository;

import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface VideoRepo extends JpaRepository<Video, Long> {

    Optional<Video> findByCode(String code);

    @Query("SELECT v FROM Video v WHERE v.videoCategory = :videoCategory AND v.lang = :lang AND v.released <= :today AND (v.archived IS NULL OR v.archived > :today) ORDER BY v.released DESC")
    List<Video> findReleasedByCategoryAndLang(@Param("videoCategory") VideoCategory videoCategory, @Param("lang") Lang lang, @Param("today") Instant today);

    @Query("SELECT v FROM Video v WHERE v.videoCategory = :videoCategory AND v.lang = :lang AND v.released <= :today AND (v.archived IS NULL OR v.archived > :today) ORDER BY v.released DESC")
    List<Video> findLatestVideosByCategoryAndLang(@Param("videoCategory") VideoCategory videoCategory, @Param("lang") Lang lang, @Param("today") Instant today, Pageable pageable);

}
