package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.VideoItemRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VideoItemServiceTest {

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    PlayListService playListService;

    @Autowired
    VideoItemRepo videoItemRepo;

    @Autowired
    VideoItemService videoItemService;

    @Test
    void existsByPlayListIdAndCode() {
        // Given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);

        PlayList playList = playListService.createPlayList(apsUser.getId(), EntityDefault.PLAYLIST_NAME);

        VideoItem videoItem = new VideoItem(EntityDefault.VIDEO_CODE);
        videoItem.setPlayList(playList);
        videoItemRepo.save(videoItem);

        // When
        boolean exists = videoItemService.existsByPlayListIdAndCode(playList.getId(), EntityDefault.VIDEO_CODE);

        // Then
        assertTrue(exists);
    }

}