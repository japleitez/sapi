package com.peecko.api.repository;

import com.peecko.api.domain.UserFavoriteVideoID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteVideoIDRepo extends JpaRepository<UserFavoriteVideoID, Long> {
    List<UserFavoriteVideoID> findByUserId(Long userId);

}
