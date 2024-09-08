package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.IdName;
import com.peecko.api.domain.dto.PlayListDTO;
import com.peecko.api.repository.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
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

        // Given
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

    @Test
    void addAndRemoveVideoItemsAtTop() {
        // Given
        VideoCategory videoCategory = EntityBuilder.buildVideoCategory();
        videoCategoryRepo.save(videoCategory);

        String videoCode1 = EntityDefault.VIDEO_CODE + "-01";
        Video video1 = EntityBuilder.buildVideo(videoCode1, videoCategory);
        videoRepo.save(video1);

        String videoCode2 = EntityDefault.VIDEO_CODE + "-02";
        Video video2 = EntityBuilder.buildVideo(videoCode2, videoCategory);
        videoRepo.save(video2);

        String videoCode3 = EntityDefault.VIDEO_CODE + "-03";
        Video video3 = EntityBuilder.buildVideo(videoCode3, videoCategory);
        videoRepo.save(video3);

        String videoCode4 = EntityDefault.VIDEO_CODE + "-04";
        Video video4 = EntityBuilder.buildVideo(videoCode4, videoCategory);
        videoRepo.save(video4);

        String videoCode5 = EntityDefault.VIDEO_CODE + "-05";
        Video video5 = EntityBuilder.buildVideo(videoCode5, videoCategory);
        videoRepo.save(video5);

        String videoCode6 = EntityDefault.VIDEO_CODE + "-06";
        Video video6 = EntityBuilder.buildVideo(videoCode6, videoCategory);
        videoRepo.save(video6);

        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);

        String listName1 = EntityDefault.PLAYLIST_NAME + "-01";
        String listName2 = EntityDefault.PLAYLIST_NAME + "-02";
        PlayList playList1 = playListService.createPlayList(apsUser.getId(), listName1);
        PlayList playList2 = playListService.createPlayList(apsUser.getId(), listName2);

        // When
        Long listId1 = playList1.getId();
        playListService.addVideoItemToTop(listId1, new VideoItem(videoCode1));
        playListService.addVideoItemToTop(listId1, new VideoItem(videoCode2));
        playListService.addVideoItemToTop(listId1, new VideoItem(videoCode3));
        playListService.addVideoItemToTop(listId1, new VideoItem(videoCode4));
        playListService.addVideoItemToTop(listId1, new VideoItem(videoCode5));
        playListService.addVideoItemToTop(listId1, new VideoItem(videoCode6));

        Long listId2 = playList2.getId();
        playListService.addVideoItemToTop(listId2, new VideoItem(videoCode1));
        playListService.addVideoItemToTop(listId2, new VideoItem(videoCode2));
        playListService.addVideoItemToTop(listId2, new VideoItem(videoCode3));
        playListService.addVideoItemToTop(listId2, new VideoItem(videoCode4));
        playListService.addVideoItemToTop(listId2, new VideoItem(videoCode5));

        // Then
        PlayListDTO playListDTO = playListService.getPlayListAsDTO(listId1, apsUser.getId());

        assertEquals(6, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode6, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(4).getCode());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(5).getCode());

        // When
        playListService.removeVideoItem(listId1, videoCode5);
        playListService.removeVideoItems(listId1, Arrays.asList(videoCode3, videoCode2));

        // Then play list 1 has been modified
        playListDTO = playListService.getPlayListAsDTO(listId1, apsUser.getId());
        assertEquals(3, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode6, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(2).getCode());

        // Then play list 2 remains unchanged
        playListDTO = playListService.getPlayListAsDTO(listId2, apsUser.getId());
        assertEquals(5, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(4).getCode());

        // When
        playListService.deletePlayList(listId1);
        boolean list1Deleted = !playListService.existsPlayList(apsUser, listName1);
        boolean list2NotDeleted = playListService.existsPlayList(apsUser, listName2);
        boolean videoItem1Deleted = !videoItemRepo.existsByPlayListIdAndCode(listId1, videoCode1);

        // Then
        assertTrue(list1Deleted);
        assertTrue(list2NotDeleted);
        assertTrue(videoItem1Deleted);
    }

    @Test
    void addAndRemoveVideoItemAtBottom() {
        // Given
        VideoCategory videoCategory = EntityBuilder.buildVideoCategory();
        videoCategoryRepo.save(videoCategory);

        String videoCode1 = EntityDefault.VIDEO_CODE + "-01";
        Video video1 = EntityBuilder.buildVideo(videoCode1, videoCategory);
        videoRepo.save(video1);

        String videoCode2 = EntityDefault.VIDEO_CODE + "-02";
        Video video2 = EntityBuilder.buildVideo(videoCode2, videoCategory);
        videoRepo.save(video2);

        String videoCode3 = EntityDefault.VIDEO_CODE + "-03";
        Video video3 = EntityBuilder.buildVideo(videoCode3, videoCategory);
        videoRepo.save(video3);

        String videoCode4 = EntityDefault.VIDEO_CODE + "-04";
        Video video4 = EntityBuilder.buildVideo(videoCode4, videoCategory);
        videoRepo.save(video4);

        String videoCode5 = EntityDefault.VIDEO_CODE + "-05";
        Video video5 = EntityBuilder.buildVideo(videoCode5, videoCategory);
        videoRepo.save(video5);

        String videoCode6 = EntityDefault.VIDEO_CODE + "-06";
        Video video6 = EntityBuilder.buildVideo(videoCode6, videoCategory);
        videoRepo.save(video6);

        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);

        String listName1 = EntityDefault.PLAYLIST_NAME + "-01";
        String listName2 = EntityDefault.PLAYLIST_NAME + "-02";
        PlayList playList1 = playListService.createPlayList(apsUser.getId(), listName1);
        PlayList playList2 = playListService.createPlayList(apsUser.getId(), listName2);

        // When
        Long listId1 = playList1.getId();
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode1));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode2));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode3));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode4));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode5));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode6));

        Long listId2 = playList2.getId();
        playListService.addVideoItemToBottom(listId2, new VideoItem(videoCode1));
        playListService.addVideoItemToBottom(listId2, new VideoItem(videoCode2));
        playListService.addVideoItemToBottom(listId2, new VideoItem(videoCode3));
        playListService.addVideoItemToBottom(listId2, new VideoItem(videoCode4));
        playListService.addVideoItemToBottom(listId2, new VideoItem(videoCode5));

        // Then
        PlayListDTO playListDTO = playListService.getPlayListAsDTO(listId1, apsUser.getId());

        assertEquals(6, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(4).getCode());
        assertEquals(videoCode6, playListDTO.getVideoItemDTOS().get(5).getCode());

        // When
        playListService.removeVideoItem(listId1, videoCode5);
        playListService.removeVideoItems(listId1, Arrays.asList(videoCode3, videoCode2));

        // Then play list 1 has been modified
        playListDTO = playListService.getPlayListAsDTO(listId1, apsUser.getId());
        assertEquals(3, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode6, playListDTO.getVideoItemDTOS().get(2).getCode());

        // Then play list 2 remains unchanged
        playListDTO = playListService.getPlayListAsDTO(listId2, apsUser.getId());
        assertEquals(5, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(4).getCode());

        // When
        playListService.moveVideoItemToTop(listId2, videoCode4);

        // Then
        playListDTO = playListService.getPlayListAsDTO(listId2, apsUser.getId());
        assertEquals(5, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(4).getCode());

        // When
        playListService.moveVideoItemToTop(listId2, videoCode5);

        // Then
        playListDTO = playListService.getPlayListAsDTO(listId2, apsUser.getId());
        assertEquals(5, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(4).getCode());


        // When
        assertDoesNotThrow(() -> playListService.moveVideoItemToTop(listId2, "wrong"));
        assertDoesNotThrow(() -> playListService.moveVideoItemToTop(-1L, videoCode1));

    }

    @Test
    void moveVideoItemBelowAnother() {
        // Given
        VideoCategory videoCategory = EntityBuilder.buildVideoCategory();
        videoCategoryRepo.save(videoCategory);

        String videoCode1 = EntityDefault.VIDEO_CODE + "-01";
        Video video1 = EntityBuilder.buildVideo(videoCode1, videoCategory);
        videoRepo.save(video1);

        String videoCode2 = EntityDefault.VIDEO_CODE + "-02";
        Video video2 = EntityBuilder.buildVideo(videoCode2, videoCategory);
        videoRepo.save(video2);

        String videoCode3 = EntityDefault.VIDEO_CODE + "-03";
        Video video3 = EntityBuilder.buildVideo(videoCode3, videoCategory);
        videoRepo.save(video3);

        String videoCode4 = EntityDefault.VIDEO_CODE + "-04";
        Video video4 = EntityBuilder.buildVideo(videoCode4, videoCategory);
        videoRepo.save(video4);

        String videoCode5 = EntityDefault.VIDEO_CODE + "-05";
        Video video5 = EntityBuilder.buildVideo(videoCode5, videoCategory);
        videoRepo.save(video5);

        String videoCode6 = EntityDefault.VIDEO_CODE + "-06";
        Video video6 = EntityBuilder.buildVideo(videoCode6, videoCategory);
        videoRepo.save(video6);

        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);

        String listName1 = EntityDefault.PLAYLIST_NAME + "-01";
        PlayList playList1 = playListService.createPlayList(apsUser.getId(), listName1);

        // When
        Long listId1 = playList1.getId();
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode1));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode2));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode3));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode4));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode5));
        playListService.addVideoItemToBottom(listId1, new VideoItem(videoCode6));

        // Then
        PlayListDTO playListDTO = playListService.getPlayListAsDTO(listId1, apsUser.getId());

        assertEquals(6, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(4).getCode());
        assertEquals(videoCode6, playListDTO.getVideoItemDTOS().get(5).getCode());

        // When
        playListService.moveVideoItemBelowAnother(listId1, videoCode2, videoCode4);

        // Then
        playListDTO = playListService.getPlayListAsDTO(listId1, apsUser.getId());
        assertEquals(6, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(4).getCode());
        assertEquals(videoCode6, playListDTO.getVideoItemDTOS().get(5).getCode());

        // When
        playListService.moveVideoItemBelowAnother(listId1, videoCode2, videoCode6);

        // Then
        playListDTO = playListService.getPlayListAsDTO(listId1, apsUser.getId());
        assertEquals(6, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode6, playListDTO.getVideoItemDTOS().get(4).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(5).getCode());

        // When
        playListService.moveVideoItemBelowAnother(listId1, videoCode2, videoCode1);

        // Then
        playListDTO = playListService.getPlayListAsDTO(listId1, apsUser.getId());
        assertEquals(6, playListDTO.getVideoItemDTOS().size());
        assertEquals(videoCode1, playListDTO.getVideoItemDTOS().get(0).getCode());
        assertEquals(videoCode2, playListDTO.getVideoItemDTOS().get(1).getCode());
        assertEquals(videoCode3, playListDTO.getVideoItemDTOS().get(2).getCode());
        assertEquals(videoCode4, playListDTO.getVideoItemDTOS().get(3).getCode());
        assertEquals(videoCode5, playListDTO.getVideoItemDTOS().get(4).getCode());
        assertEquals(videoCode6, playListDTO.getVideoItemDTOS().get(5).getCode());

    }

}