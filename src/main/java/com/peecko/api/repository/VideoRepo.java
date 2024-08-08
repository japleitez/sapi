package com.peecko.api.repository;

import com.peecko.api.domain.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepo extends JpaRepository<Video, Long> {

    @Query("SELECT v FROM Video v WHERE v.videoCategory IN :categories AND v.released IS NOT NULL ORDER BY v.released DESC")
    List<Video> findTopByCategoriesOrderByUploadDateDesc(@Param("categories") List<Long> categories, @Param("limit") int limit);

}
