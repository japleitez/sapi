package com.peecko.api.repository;

import com.peecko.api.domain.PlayList;
import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoItemRepo extends JpaRepository<VideoItem, String> {

    Optional<VideoItem> findByPlayListAndVideo(PlayList playlist, Video video);
    List<VideoItem> findByPlayListAndVideoCodeIn(PlayList playlist, List<String> videoCodes);

    @Query("select count(vi) > 0 from VideoItem vi where vi.playList.id = :playListId and vi.video.code = :videoCode")
    boolean existsVideoItem(@Param("playListId") Long playListId, @Param("videoCode") String videoCode);

    @Query(value = "SELECT * FROM video_item v WHERE v.play_list_id = :playlistId AND v.previous_video_item_id IS NULL LIMIT 1", nativeQuery = true)
    Optional<VideoItem> findFirstVideoItemOfPlaylist(@Param("playlistId") Long playlistId);

    @Query(value = "SELECT * FROM video_item v WHERE v.play_list_id = :playlistId AND v.next_video_item_id IS NULL LIMIT 1", nativeQuery = true)
    Optional<VideoItem> findLastVideoItemOfPlaylist(@Param("playlistId") Long playlistId);

}
