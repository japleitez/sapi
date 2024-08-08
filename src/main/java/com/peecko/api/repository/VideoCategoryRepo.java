package com.peecko.api.repository;

import com.peecko.api.domain.VideoCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoCategoryRepo extends JpaRepository<VideoCategory, Long> {
    List<VideoCategory> findByReleasedIsNotNullAndArchivedIsNull();

}
