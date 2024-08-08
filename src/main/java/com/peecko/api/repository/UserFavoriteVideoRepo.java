package com.peecko.api.repository;

import com.peecko.api.domain.UserFavoriteVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteVideoRepo extends JpaRepository<UserFavoriteVideo, Long> {
    List<UserFavoriteVideo> findByUserId(Long userId);

}
