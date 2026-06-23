package com.peecko.api.repository;

import com.peecko.api.domain.PlayListItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayListItemRepo extends JpaRepository<PlayListItem, Long> {

    @Query("select count(pi) > 0 from PlayListItem pi where pi.playList.id = :playListId and pi.video.code = :videoCode")
    boolean existsVideoItem(@Param("playListId") Long playListId, @Param("videoCode") String videoCode);

    @Query("SELECT pi FROM PlayListItem pi JOIN FETCH pi.video WHERE pi.playList.id = :playListId ORDER BY pi.position ASC")
    List<PlayListItem> findByPlayListIdWithVideoOrderByPositionAsc(@Param("playListId") Long playListId);

    @Query("SELECT pi FROM PlayListItem pi JOIN FETCH pi.video WHERE pi.playList.id = :playListId ORDER BY pi.position ASC")
    List<PlayListItem> findByPlayListIdOrderByPositionAsc(@Param("playListId") Long playListId);

    @Query("SELECT pi FROM PlayListItem pi JOIN FETCH pi.video WHERE pi.playList.id = :playListId AND pi.video.code = :videoCode")
    Optional<PlayListItem> findByPlayListIdAndVideoCode(@Param("playListId") Long playListId, @Param("videoCode") String videoCode);

    @Query("SELECT MAX(pi.position) FROM PlayListItem pi WHERE pi.playList.id = :playListId")
    Optional<Integer> findMaxPositionByPlayListId(@Param("playListId") Long playListId);

}
