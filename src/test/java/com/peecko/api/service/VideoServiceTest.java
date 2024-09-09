package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.enumeration.Player;
import com.peecko.api.repository.*;
import com.peecko.api.utils.InstantUtils;
import com.peecko.api.utils.LabelUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VideoServiceTest {

    @Autowired
    VideoMapper videoMapper;

    @Autowired
    VideoRepo videoRepo;

    @Autowired
    LabelRepo labelRepo;

    @Autowired
    LabelService labelService;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    TodayVideoRepo todayVideoRepo;

    @Autowired
    VideoCategoryRepo videoCategoryRepo;

    @Autowired
    UserFavoriteVideoRepo userFavoriteVideoRepo;

    @Autowired
    VideoService videoService;

    // data references

    VideoCategory category1 = null;
    VideoCategory category2 = null;
    VideoCategory category3 = null;

    Video video11En = null;
    Video video12En = null;
    Video video21En = null;
    Video video22En = null;
    Video video23En = null;
    Video video24EnAr = null;
    Video video25Fr = null;
    Video video26En = null;
    Video video27En = null;

    Label yoga = null;
    Label pilates = null;
    Label flexibility = null;

    Label all = null;
    Label relax = null;
    Label energy = null;
    Label strength = null;

    Instant created = InstantUtils.createInstantFromDays(-20);
    Instant released = InstantUtils.createInstantFromDays(-10);
    Instant archived = InstantUtils.createInstantFromDays(-5);


    @Test
    void getCachedTodayVideos() {
        // Given
        createVideos();

        List<Video> videos = List.of(video11En, video12En, video21En, video22En, video23En, video24EnAr, video25Fr, video26En, video27En);
        Set<Long> englishVideoIds = videos.stream().filter(v -> v.getLanguage() == Lang.EN && v.getArchived() == null).map(Video::getId).collect(Collectors.toSet());
        TodayVideo todayVideoEN = new TodayVideo(Lang.EN, LocalDate.now(), englishVideoIds);
        todayVideoRepo.save(todayVideoEN);

        Set<Long> frenchVideoIds = videos.stream().filter(v -> v.getLanguage() == Lang.FR && v.getArchived() == null).map(Video::getId).collect(Collectors.toSet());
        TodayVideo todayVideoFR = new TodayVideo(Lang.FR, LocalDate.now(), frenchVideoIds);
        todayVideoRepo.save(todayVideoFR);

        // extra data to test latest today videos is returned
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        TodayVideo extra = new TodayVideo(Lang.EN, twoDaysAgo, Set.of(video11En.getId(), video12En.getId()));
        todayVideoRepo.save(extra);


        // When
        List<Video> englishResult = videoService.getCachedTodayVideos(Lang.EN);
        List<Video> frenchResult = videoService.getCachedTodayVideos(Lang.FR);

        // Then
        assertEquals(7, englishResult.size());
        assertEquals(1, frenchResult.size());
        assertEquals(englishVideoIds, englishResult.stream().map(Video::getId).collect(Collectors.toSet()));
        assertEquals(frenchVideoIds, frenchResult.stream().map(Video::getId).collect(Collectors.toSet()));

        // When
        List<Video> englishResult2 = videoService.getCachedTodayVideos(Lang.EN);

        // Then
        boolean cacheHit = englishResult == englishResult2;
        assertTrue(cacheHit);
    }

    @Test
    void getCachedLatestVideo() {
        // Given
        createVideos();
        // When
        Map<VideoCategory, List<Video>> result = videoService.getCachedLatestVideo(Lang.EN);

        // Then
        assertNull(result.get(category3));
        assertEquals(2, result.get(category1).size());
        assertEquals(VideoService.CATEGORY_VIDEOS_SIZE, result.get(category2).size());

        // When
        Map<VideoCategory, List<Video>> result2 = videoService.getCachedLatestVideo(Lang.EN);

        // Then
        boolean cacheHit = result == result2;
        assertTrue(cacheHit);

    }

    @Test
    void getCachedVideosByCategoryAndLang() {
        // Given
        createVideos();
        List<Video> videos = List.of(video21En, video22En, video23En, video24EnAr, video25Fr, video26En, video27En);
        int count = (int) videos.stream().filter(v -> v.getLanguage() == Lang.EN && v.getArchived() == null).count();

        // When
        List<Video> result = videoService.getCachedVideosByCategoryAndLang(category2, Lang.EN);

        // Then
        assertEquals(count, result.size());

        // When
        List<Video> result2 = videoService.getCachedVideosByCategoryAndLang(category2, Lang.EN);

        // Then
        boolean cacheHit = result == result2;
        assertTrue(cacheHit);
    }

    @Test
    void getVideoTags() {

        // Given
        createVideos();

        // When
        List<Video> videos = videoService.getCachedVideosByCategoryAndLang(category2, Lang.EN);
        List<VideoDTO> videoDTOS = videoService.toVideoDTOs(videos, Lang.EN);
        List<String> tags = videoService.getVideoTags(videoDTOS, Lang.EN);

        // Then
        assertEquals(3, tags.size());
        assertTrue(tags.contains(all.getText()));
        assertTrue(tags.contains(energy.getText()));
        assertTrue(tags.contains(strength.getText()));

    }

    @Test
    void toVideoDTOs() {
        // Given
        createVideos();

        // When
        List<Video> videos = videoService.getCachedVideosByCategoryAndLang(category2, Lang.EN);
        List<VideoDTO> videoDTOS = videoService.toVideoDTOs(videos, Lang.EN);

        // Then
        assertEquals(6, videoDTOS.size());
    }

    @Test
    void resolveFavorites() {
    }

    @Test
    void testResolveFavorites() {
    }

    @Test
    void toCategoryDTOs() {
    }

    @Test
    void toCategoryDTO() {
    }

    @Test
    void addUserFavoriteVideo() {
    }

    @Test
    void removeUserFavoriteVideo() {
    }

    @Test
    void deleteFavoriteVideosForUser() {
    }

    @Test
    void findUserFavoriteVideos() {
    }

    @Test
    void existsByCode() {
    }

    private void createVideos() {

        // YOGA
        yoga = labelRepo.save(new Label(Lang.EN, "vc.yoga", "Yoga"));
        pilates = labelRepo.save(new Label(Lang.EN, "vc.pilates", "Pilates"));
        flexibility = labelRepo.save(new Label(Lang.EN, "vc.flexibility", "Flexibility"));

        all = labelRepo.save(new Label(Lang.EN, "v.all", "All"));
        relax = labelRepo.save(new Label(Lang.EN, "v.relax", "Relax"));
        energy = labelRepo.save(new Label(Lang.EN, "v.enery", "Energy"));
        strength = labelRepo.save(new Label(Lang.EN, "v.strength", "Strength"));

        category1 = new VideoCategory();
        category1.setCode("YOGA");
        category1.setTitle("Yoga");
        category1.setLabel(yoga.getCode());
        category1.setCreated(created);
        category1.setReleased(released);
        category1.setArchived(null);
        videoCategoryRepo.save(category1);


        video11En = new Video();
        video11En.setVideoCategory(category1);
        video11En.setCode("YOGA_1010");
        video11En.setTitle("Yoga #1010");
        video11En.setDescription("Morning yoga #1010");
        video11En.setFilename("1010.mov");
        video11En.setLanguage(Lang.EN);
        video11En.setPlayer(Player.PEECKO);
        video11En.setThumbnail("http://peecko/thumbnail/1010.jpg");
        video11En.setUrl("http://peecko/video/1010");
        video11En.setCreated(created);
        video11En.setReleased(released);
        video11En.setArchived(null);
        video11En.setDuration(1010);
        video11En.setTags(LabelUtils.concatCodes(all, relax));
        videoRepo.save(video11En);

        video12En = new Video();
        video12En.setVideoCategory(category1);
        video12En.setCode("YOGA_1020");
        video12En.setTitle("Yoga #1020");
        video12En.setDescription("Morning yoga #1020");
        video12En.setFilename("1020.mov");
        video12En.setLanguage(Lang.EN);
        video12En.setPlayer(Player.PEECKO);
        video12En.setThumbnail("http://peecko/thumbnail/1020.jpg");
        video12En.setUrl("http://peecko/video/1020");
        video12En.setCreated(Instant.now());
        video12En.setReleased(Instant.now());
        video12En.setArchived(null);
        video12En.setDuration(1020);
        video12En.setTags(LabelUtils.concatCodes(all, relax));
        videoRepo.save(video12En);

        // PILATES
        category2 = new VideoCategory();
        category2.setCode("PILATES");
        category2.setTitle("Pilates");
        category2.setLabel(pilates.getCode());
        category2.setCreated(created);
        category2.setReleased(released);
        category2.setArchived(null);
        videoCategoryRepo.save(category2);

        video21En = new Video();
        video21En.setVideoCategory(category2);
        video21En.setCode("PILATES_2010");
        video21En.setTitle("Pilates #2010");
        video21En.setDescription("Evening pilates #2010");
        video21En.setFilename("2010.mov");
        video21En.setLanguage(Lang.EN);
        video21En.setPlayer(Player.PEECKO);
        video21En.setThumbnail("http://peecko/thumbnail/2010.jpg");
        video21En.setUrl("http://peecko/video/2010");
        video21En.setCreated(created);
        video21En.setReleased(released);
        video21En.setArchived(null);
        video21En.setDuration(2010);
        video21En.setTags(LabelUtils.concatCodes(all, energy, strength));
        videoRepo.save(video21En);

        video22En = new Video();
        video22En.setVideoCategory(category2);
        video22En.setCode("PILATES_2020");
        video22En.setTitle("Pilates #2020");
        video22En.setDescription("Evening pilates #2020");
        video22En.setFilename("2020.mov");
        video22En.setLanguage(Lang.EN);
        video22En.setPlayer(Player.PEECKO);
        video22En.setThumbnail("http://peecko/thumbnail/2020.jpg");
        video22En.setUrl("http://peecko/video/2020");
        video22En.setCreated(created);
        video22En.setReleased(released);
        video22En.setArchived(null);
        video22En.setDuration(2020);
        video22En.setTags(LabelUtils.concatCodes(all, energy, strength));
        videoRepo.save(video22En);

        video23En = new Video();
        video23En.setVideoCategory(category2);
        video23En.setCode("PILATES_2030");
        video23En.setTitle("Pilates #2030");
        video23En.setDescription("Evening pilates #2030");
        video23En.setFilename("2030.mov");
        video23En.setLanguage(Lang.EN);
        video23En.setPlayer(Player.PEECKO);
        video23En.setThumbnail("http://peecko/thumbnail/2030.jpg");
        video23En.setUrl("http://peecko/video/2030");
        video23En.setCreated(created);
        video23En.setReleased(released);
        video23En.setArchived(null);
        video23En.setDuration(2030);
        video23En.setTags(LabelUtils.concatCodes(all, energy, strength));
        videoRepo.save(video23En);

        video24EnAr = new Video(); // diff -> archived
        video24EnAr.setVideoCategory(category2);
        video24EnAr.setCode("PILATES_2040");
        video24EnAr.setTitle("Pilates #2040");
        video24EnAr.setDescription("Evening pilates #2040");
        video24EnAr.setFilename("2040.mov");
        video24EnAr.setLanguage(Lang.EN);
        video24EnAr.setPlayer(Player.PEECKO);
        video24EnAr.setThumbnail("http://peecko/thumbnail/2040.jpg");
        video24EnAr.setUrl("http://peecko/video/2040");
        video24EnAr.setCreated(created);
        video24EnAr.setReleased(released);
        video24EnAr.setArchived(archived);
        video24EnAr.setDuration(2040);
        video24EnAr.setTags(LabelUtils.concatCodes(all, energy, strength));
        videoRepo.save(video24EnAr);

        video25Fr = new Video(); // diff -> FR
        video25Fr.setVideoCategory(category2);
        video25Fr.setCode("PILATES_2050");
        video25Fr.setTitle("Pilates #2050");
        video25Fr.setDescription("Evening pilates #2050");
        video25Fr.setFilename("2050.mov");
        video25Fr.setLanguage(Lang.FR);
        video25Fr.setPlayer(Player.PEECKO);
        video25Fr.setThumbnail("http://peecko/thumbnail/2050.jpg");
        video25Fr.setUrl("http://peecko/video/2050");
        video25Fr.setCreated(created);
        video25Fr.setReleased(released);
        video25Fr.setArchived(null);
        video25Fr.setDuration(2050);
        video25Fr.setTags(LabelUtils.concatCodes(all, energy, strength));
        videoRepo.save(video25Fr);

        video26En = new Video();
        video26En.setVideoCategory(category2);
        video26En.setCode("PILATES_2060");
        video26En.setTitle("Pilates #260");
        video26En.setDescription("Evening pilates #2060");
        video26En.setFilename("2060.mov");
        video26En.setLanguage(Lang.EN);
        video26En.setPlayer(Player.PEECKO);
        video26En.setThumbnail("http://peecko/thumbnail/2060.jpg");
        video26En.setUrl("http://peecko/video/2060");
        video26En.setCreated(created);
        video26En.setReleased(released);
        video26En.setArchived(null);
        video26En.setDuration(2060);
        video26En.setTags(LabelUtils.concatCodes(all, energy, strength));
        videoRepo.save(video26En);

        video27En = new Video();
        video27En.setVideoCategory(category2);
        video27En.setCode("PILATES_2070");
        video27En.setTitle("Pilates #2060");
        video27En.setDescription("Evening pilates #2070");
        video27En.setFilename("2070.mov");
        video27En.setLanguage(Lang.EN);
        video27En.setPlayer(Player.PEECKO);
        video27En.setThumbnail("http://peecko/thumbnail/2070.jpg");
        video27En.setUrl("http://peecko/video/2070");
        video27En.setCreated(created);
        video27En.setReleased(released);
        video27En.setArchived(null);
        video27En.setDuration(2070);
        video27En.setTags(LabelUtils.concatCodes(all, energy, strength));
        videoRepo.save(video27En);

        // FLEXIBILITY
        category3 = new VideoCategory(); // diff -> archived
        category3.setCode("FLEXIBILITY");
        category3.setTitle("Flexibility");
        category3.setLabel(flexibility.getCode());
        category3.setCreated(created);
        category3.setReleased(released);
        category3.setArchived(archived);
        videoCategoryRepo.save(category3);

    }


}
