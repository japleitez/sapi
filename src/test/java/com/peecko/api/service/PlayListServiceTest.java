package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.IdName;
import com.peecko.api.domain.dto.PlayListDTO;
import com.peecko.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PlayListServiceTest {

    @Autowired
    ApsUserRepo apsUserRepo;

    @Autowired
    VideoMapper videoMapper;

    @Autowired
    PlayListRepo playListRepo;

    @Autowired
    VideoItemRepo videoItemRepo;

    @Autowired
    VideoRepo videoRepo;

    @Autowired
    VideoCategoryRepo videoCategoryRepo;

    @Autowired
    UserFavoriteVideoRepo userFavoriteVideoRepo;

    @Autowired
    PlayListService playListService;

    @BeforeEach
    void beforeEach() {
        playListRepo.deleteAll();
        playListRepo.flush();
        apsUserRepo.deleteAll();
        apsUserRepo.flush();
    }

    @Test
    void createPlayList() {
        // Given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);

        // When
        PlayList playList = playListService.createPlayList(apsUser.getId(), EntityDefault.PLAYLIST_NAME);
        PlayListDTO playListDTO =  playListService.toPlayListDTO(playList);

        // Then
        assertEquals(EntityDefault.PLAYLIST_NAME, playList.getName());
        assertEquals(EntityDefault.PLAYLIST_NAME, playListDTO.getName());
        assertEquals(playList.getId(), playListDTO.getId());
    }

    @Test
    void existsPlayList() {

        // Given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);
        PlayList playlist = playListService.createPlayList(apsUser.getId(), EntityDefault.PLAYLIST_NAME);

        // When
        boolean exists = playListService.existsPlayList(apsUser, EntityDefault.PLAYLIST_NAME);
        boolean deleted = false;
        if (exists) {
            playListService.deletePlayList(playlist.getId());
            deleted = !playListService.existsPlayList(apsUser, EntityDefault.PLAYLIST_NAME);
        }

        // Then
        assertTrue(exists);
        assertTrue(deleted);
    }



    @Test
    void getPlayListsAsIdNames() {
        // Given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);

        // Given names in descending order
        String name4 = EntityDefault.PLAYLIST_NAME + "-04";
        String name3 = EntityDefault.PLAYLIST_NAME + "-03";
        String name2 = EntityDefault.PLAYLIST_NAME + "-02";
        String name1 = EntityDefault.PLAYLIST_NAME + "-01";
        playListService.createPlayList(apsUser.getId(), name4);
        playListService.createPlayList(apsUser.getId(), name3);
        playListService.createPlayList(apsUser.getId(), name2);
        playListService.createPlayList(apsUser.getId(), name1);

        // When
        List<IdName> idNames = playListService.getPlayListsAsIdNames(apsUser);

        // Then names in ascending order
        assertEquals(4, idNames.size());
        assertEquals(name1, idNames.get(0).getName());
        assertEquals(name2, idNames.get(1).getName());
        assertEquals(name3, idNames.get(2).getName());
        assertEquals(name4, idNames.get(3).getName());
    }

    @Test
    void getPlayListAsDTO() {
        // Given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);
        PlayList playlist = playListService.createPlayList(apsUser.getId(), EntityDefault.PLAYLIST_NAME);

        // When
        PlayListDTO playListDTO = playListService.getPlayListAsDTO(playlist.getId(), apsUser.getId());

        // Then
        assertEquals(playlist.getId(), playListDTO.getId());
        assertEquals(playlist.getName(), playListDTO.getName());
    }

    @Test
    void moveVideoItemBelowAnother() {
    }

    @Test
    void addAndRemoveVideoItems() {
        // Given
        VideoCategory videoCategory = EntityBuilder.buildVideoCategory();
        videoCategoryRepo.save(videoCategory);

        String code1 = EntityDefault.VIDEO_CODE + "-01";
        Video video1 = EntityBuilder.buildVideo(code1, videoCategory);
        videoRepo.save(video1);

        String code2 = EntityDefault.VIDEO_CODE + "-02";
        Video video2 = EntityBuilder.buildVideo(code2, videoCategory);
        videoRepo.save(video2);

        String code3 = EntityDefault.VIDEO_CODE + "-03";
        Video video3 = EntityBuilder.buildVideo(code3, videoCategory);
        videoRepo.save(video3);

        String code4 = EntityDefault.VIDEO_CODE + "-04";
        Video video4 = EntityBuilder.buildVideo(code4, videoCategory);
        videoRepo.save(video4);

        String code5 = EntityDefault.VIDEO_CODE + "-05";
        Video video5 = EntityBuilder.buildVideo(code5, videoCategory);
        videoRepo.save(video5);

        String code6 = EntityDefault.VIDEO_CODE + "-06";
        Video video6 = EntityBuilder.buildVideo(code6, videoCategory);
        videoRepo.save(video6);

        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);

        String list1 = EntityDefault.PLAYLIST_NAME + "-01";
        String list2 = EntityDefault.PLAYLIST_NAME + "-02";
        PlayList playList1 = playListService.createPlayList(apsUser.getId(), list1);
        PlayList playList2 = playListService.createPlayList(apsUser.getId(), list2);

        // When
        Long L1 = playList1.getId();
        playListService.addVideoItemToTop(L1, new VideoItem(code1));
        playListService.addVideoItemToTop(L1, new VideoItem(code2));
        playListService.addVideoItemToTop(L1, new VideoItem(code3));
        playListService.addVideoItemToTop(L1, new VideoItem(code4));
        playListService.addVideoItemToTop(L1, new VideoItem(code5));
        playListService.addVideoItemToTop(L1, new VideoItem(code6));

        Long L2 = playList2.getId();
        playListService.addVideoItemToTop(L2, new VideoItem(code1));
        playListService.addVideoItemToTop(L2, new VideoItem(code2));
        playListService.addVideoItemToTop(L2, new VideoItem(code3));
        playListService.addVideoItemToTop(L2, new VideoItem(code4));
        playListService.addVideoItemToTop(L2, new VideoItem(code5));

        // Then
        PlayListDTO playListDTO = playListService.getPlayListAsDTO(L1, apsUser.getId());

        assertEquals(6, playListDTO.getVideoItemDTOS().size());
        assertEquals(code6, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(code5, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(code4, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(code3, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(code2, playListDTO.getVideoItemDTOS().get(4).getCode());
        assertEquals(code1, playListDTO.getVideoItemDTOS().get(5).getCode());

        // When
        playListService.removeVideoItem(L1, code5);
        playListService.removeVideoItems(L1, Arrays.asList(code3, code2));

        // Then play list 1 has been modified
        playListDTO = playListService.getPlayListAsDTO(L1, apsUser.getId());
        assertEquals(3, playListDTO.getVideoItemDTOS().size());
        assertEquals(code6, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(code4, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(code1, playListDTO.getVideoItemDTOS().get(2).getCode());

        // Then play list 2 remains unchanged
        playListDTO = playListService.getPlayListAsDTO(L2, apsUser.getId());
        assertEquals(5, playListDTO.getVideoItemDTOS().size());
        assertEquals(code5, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(code4, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(code3, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(code2, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(code1, playListDTO.getVideoItemDTOS().get(4).getCode());

    }


    @Test
    void existsById() {
        // Given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);

        PlayList playList = playListService.createPlayList(apsUser.getId(), EntityDefault.PLAYLIST_NAME);

        // When
        boolean result = playListService.existsById(playList.getId());

        // Then
        assertTrue(result);
    }

}