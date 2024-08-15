package com.peecko.api.repository;

import com.peecko.api.domain.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VideoCategoryRepo extends JpaRepository<VideoCategory, Long> {
    @Query("SELECT vc.id FROM VideoCategory vc WHERE vc.released <= :today AND vc.archived IS NULL")
    List<Long> findReleasedAsOfToday(Instant today);

}
