package com.peecko.api.repository;

import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VideoRepo extends JpaRepository<Video, Long> {

    List<Video> findByIdIn(Set<Long> ids);

    Optional<Video> findByCode(String code);

    @Query("SELECT COUNT(v) > 0 FROM Video v WHERE v.code = :code")
    boolean existsByCode(@Param("code") String code);

    @Query("SELECT v FROM Video v WHERE v.code IN :videoCodes")
    Set<Video> findByCodes(@Param("videoCodes") List<String> videoCodes);

    @Query("SELECT v FROM Video v WHERE v.videoCategory = :videoCategory AND v.lang = :lang AND v.released <= :today AND (v.archived IS NULL OR v.archived > :today) ORDER BY v.released DESC")
    List<Video> findByCategoryAndLang(@Param("videoCategory") VideoCategory videoCategory, @Param("lang") Lang lang, @Param("today") LocalDate today);

    @Query("SELECT v FROM Video v WHERE v.videoCategory = :videoCategory AND v.lang = :lang AND v.released <= :today AND (v.archived IS NULL OR v.archived > :today) ORDER BY v.released DESC")
    List<Video> findLatestByCategoryAndLang(@Param("videoCategory") VideoCategory videoCategory, @Param("lang") Lang lang, @Param("today") LocalDate today, Pageable pageable);

}
