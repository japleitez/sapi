package com.peecko.api.repository;

import com.peecko.api.domain.VideoItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Repository
public interface VideoItemRepo extends JpaRepository<VideoItem, String> {

    Optional<VideoItem> findByPlayListIdAndCode(Long playListId, String code);

    boolean existsByPlayListIdAndCode(Long playListId, String code);

}
