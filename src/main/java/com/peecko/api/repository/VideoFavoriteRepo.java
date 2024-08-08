package com.peecko.api.repository;

import com.peecko.api.domain.VideoFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoFavoriteRepo extends JpaRepository<VideoFavorite, Long> {
    List<VideoFavorite> findByUserId(Long userId);
}
