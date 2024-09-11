package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.CategoryDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.enumeration.Player;
import com.peecko.api.repository.*;
import com.peecko.api.utils.InstantUtils;
import com.peecko.api.utils.LabelUtils;
import com.peecko.api.utils.NameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VideoServiceTest {

    @Autowired
    ApsUserRepo apsUserRepo;

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

    VideoCategory yogaCategory = null;
    VideoCategory pilatesCategory = null;
    VideoCategory flexibilityCategory = null;

    Video yoga1En = null;
    Video yoga2En = null;
    Video pilates1En = null;
    Video pilates2En = null;
    Video pilates3En = null;
    Video pilates4En = null;
    Video pilates5En = null;
    Video pilatesFrench = null;
    Video pilatesArchived = null;

    Label yoga = null;
    Label pilates = null;
    Label flexibility = null;

    Label all = null;
    Label relax = null;
    Label energy = null;
    Label strength = null;

    Instant created = InstantUtils.createInstantFromDays(-20);
    LocalDate released = LocalDate.now().minusDays(10);
    LocalDate archived = LocalDate.now().minusDays(5);

    List<Video> videosYoga = null;
    List<Video> videosPilates = null;

    List<Video> allVideos = null;
    Set<Long> activeAllEnIds = null;
    Set<Long> activeAllFrIds = null;
    Set<Long> activeYogaEnIds = null;
    Set<Long> activePilatesEnIds = null;

    @BeforeEach
    void setUp() {
        videoService.clearAllCaches();
        createVideos();
    }

    @Test
    void getCachedTodayVideos() {
        // Given
        TodayVideo todayVideoEn = new TodayVideo(Lang.EN, LocalDate.now(), activeAllEnIds);
        todayVideoRepo.save(todayVideoEn);

        TodayVideo todayVideoFr = new TodayVideo(Lang.FR, LocalDate.now(), activeAllFrIds);
        todayVideoRepo.save(todayVideoFr);

        // additional today_video to test that videoService.getCachedTodayVideos returns the latest today_video
        LocalDate twoDaysAgo = LocalDate.now().minusDays(2);
        TodayVideo extra = new TodayVideo(Lang.EN, twoDaysAgo, Set.of(yoga1En.getId(), yoga2En.getId()));
        todayVideoRepo.save(extra);


        // When
        List<Video> todayVideoEnResult = videoService.getCachedTodayVideos(Lang.EN);
        List<Video> todayVideoFrResult = videoService.getCachedTodayVideos(Lang.FR);

        // Then
        assertEquals(activeAllEnIds.size(), todayVideoEnResult.size());
        assertEquals(activeAllFrIds.size(), todayVideoFrResult.size());
        assertEquals(activeAllEnIds, todayVideoEnResult.stream().map(Video::getId).collect(Collectors.toSet()));
        assertEquals(activeAllFrIds, todayVideoFrResult.stream().map(Video::getId).collect(Collectors.toSet()));

        // When
        List<Video> englishResult2 = videoService.getCachedTodayVideos(Lang.EN);

        // Then
        boolean cacheHit = todayVideoEnResult == englishResult2;
        assertTrue(cacheHit);
    }

    @Test
    void getCachedLatestVideo() {
        // Given
        int expectedYogaCount = Math.min(activeYogaEnIds.size(), VideoService.CATEGORY_VIDEOS_MAX_SIZE);
        int expectedPilatesCount = Math.min(activePilatesEnIds.size(), VideoService.CATEGORY_VIDEOS_MAX_SIZE);

        // When
        Map<VideoCategory, List<Video>> result = videoService.getCachedLatestVideo(Lang.EN);
        Map<VideoCategory, List<Video>> result2 = videoService.getCachedLatestVideo(Lang.EN);

        // Then
        boolean cacheHit = result == result2;
        assertTrue(cacheHit);
        assertNull(result.get(flexibilityCategory));
        assertEquals(expectedYogaCount, result.get(yogaCategory).size());
        assertEquals(expectedPilatesCount, result.get(pilatesCategory).size());
    }

    @Test
    void getCachedVideosByCategoryAndLang() {
        // When
        List<Video> result = videoService.getCachedVideosByCategoryAndLang(pilatesCategory, Lang.EN);
        List<Video> result2 = videoService.getCachedVideosByCategoryAndLang(pilatesCategory, Lang.EN);

        // Then
        boolean cacheHit = result == result2;
        assertTrue(cacheHit);
        assertEquals(activePilatesEnIds.size(), result.size());
        assertEquals(activePilatesEnIds, result.stream().map(Video::getId).collect(Collectors.toSet()));
    }

    @Test
    void getVideoTags() {
        // When
        List<Video> videos = videoService.getCachedVideosByCategoryAndLang(pilatesCategory, Lang.EN);
        List<VideoDTO> videoDTOs = videoService.toVideoDTOs(videos, Lang.EN);
        List<String> tags = videoService.getVideoTags(videoDTOs, Lang.EN);

        // Then
        assertEquals(3, tags.size());
        assertTrue(tags.contains(all.getText()));
        assertTrue(tags.contains(energy.getText()));
        assertTrue(tags.contains(strength.getText()));
    }

    @Test
    void toVideoDTOs() {
        // When
        List<Video> videosEn = videoService.getCachedVideosByCategoryAndLang(pilatesCategory, Lang.EN);
        List<VideoDTO> videosEnDTOs = videoService.toVideoDTOs(videosEn, Lang.EN);

        // Then
        assertEquals(activePilatesEnIds.size(), videosEn.size());
        assertEquals(videosEn.size(), videosEnDTOs.size());
    }

    @Test
    void resolveFavorites() {
        // Given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUser.setLanguage(Lang.EN);
        apsUserRepo.save(apsUser);
        apsUserRepo.flush();

        // add 1 yoga video and 1 pilates video to user's favorites
        int yogaFavoriteCount = 1;
        int pilatesFavoriteCount = 1;
        videoService.addUserFavoriteVideo(apsUser.getId(), yoga1En.getCode());
        videoService.addUserFavoriteVideo(apsUser.getId(), pilates1En.getCode());

        // When
        List<Video> videosYogaEn = videoService.getCachedVideosByCategoryAndLang(yogaCategory, Lang.EN);
        List<Video> videosPilatesEn = videoService.getCachedVideosByCategoryAndLang(pilatesCategory, Lang.EN);
        videoService.resolveFavorites(videosYogaEn, apsUser.getId());
        videoService.resolveFavorites(videosPilatesEn, apsUser.getId());

        // Then
        assertEquals(yogaFavoriteCount, videosYogaEn.stream().filter(Video::isFavorite).count());
        assertEquals(pilatesFavoriteCount, videosPilatesEn.stream().filter(Video::isFavorite).count());
        assertEquals(yoga1En, videosYogaEn.stream().filter(Video::isFavorite).findFirst().orElse(null));
        assertEquals(pilates1En, videosPilatesEn.stream().filter(Video::isFavorite).findFirst().orElse(null));

        // When
        Map<VideoCategory, List<Video>> result = videoService.getCachedLatestVideo(Lang.EN);
        videoService.resolveFavorites(result, apsUser.getId());

        // Then
        assertEquals(yogaFavoriteCount, result.get(yogaCategory).stream().filter(Video::isFavorite).count());
        assertEquals(pilatesFavoriteCount, result.get(pilatesCategory).stream().filter(Video::isFavorite).count());
        assertEquals(yoga1En, result.get(yogaCategory).stream().filter(Video::isFavorite).findFirst().orElse(null));
        assertEquals(pilates1En, result.get(pilatesCategory).stream().filter(Video::isFavorite).findFirst().orElse(null));

        // When
        videoService.removeUserFavoriteVideo(apsUser.getId(), yoga1En.getCode());
        videoService.resolveFavorites(result.get(yogaCategory), apsUser.getId());
        videoService.resolveFavorites(result.get(pilatesCategory), apsUser.getId());

        // Then
        assertEquals(0, result.get(yogaCategory).stream().filter(Video::isFavorite).count());
        assertEquals(1, result.get(pilatesCategory).stream().filter(Video::isFavorite).count());

    }

    @Test
    void toCategoryDTOs() {
        // Given
        int expectedYogaEnCount = Math.min(activeYogaEnIds.size(), VideoService.CATEGORY_VIDEOS_MAX_SIZE);
        int expectedPilatesEnCount = Math.min(activePilatesEnIds.size(), VideoService.CATEGORY_VIDEOS_MAX_SIZE);

        // When
        Map<VideoCategory, List<Video>> categoryVideos = videoService.getCachedLatestVideo(Lang.EN);
        List<CategoryDTO> categoryDTOS = videoService.toCategoryDTOs(categoryVideos, Lang.EN);

        // Then
        CategoryDTO yogaCategoryDTO = getCategoryDTO(categoryDTOS, yogaCategory.getCode());
        CategoryDTO pilatesCategoryDTO = getCategoryDTO(categoryDTOS, pilatesCategory.getCode());
        assertEquals(expectedYogaEnCount, yogaCategoryDTO.getVideos().size());
        assertEquals(expectedPilatesEnCount, pilatesCategoryDTO.getVideos().size());
    }

    @Test
    void deleteFavoriteVideosForUser() {
        // Given
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUser.setLanguage(Lang.EN);
        apsUserRepo.save(apsUser);
        apsUserRepo.flush();

        // add 2 yoga videos to user's favorites
        int yogaFavoriteCount = 2;
        videoService.addUserFavoriteVideo(apsUser.getId(), yoga1En.getCode());
        videoService.addUserFavoriteVideo(apsUser.getId(), yoga2En.getCode());

        // When
        List<Video> favoriteVideos = videoService.findUserFavoriteVideos(apsUser.getId());

        // Then
        assertEquals(yogaFavoriteCount, favoriteVideos.size());
        assertTrue(favoriteVideos.stream().anyMatch(video -> video.getId().equals(yoga1En.getId())));
        assertTrue(favoriteVideos.stream().anyMatch(video -> video.getId().equals(yoga2En.getId())));

        // When
        videoService.deleteFavoriteVideosForUser(apsUser.getId());

        // Then
        List<Video> favoriteVideos2 = videoService.findUserFavoriteVideos(apsUser.getId());
        assertTrue(favoriteVideos2.isEmpty());
    }

    @Test
    void existsByCode() {
        // When
        boolean exists = videoService.existsByCode(yoga1En.getCode());

        // Then
        assertTrue(exists);
    }

    private CategoryDTO getCategoryDTO(List<CategoryDTO> categoryDTOS, String categoryCode) {
        return categoryDTOS.stream()
              .filter(dto -> dto.getCode().equals(categoryCode))
              .findFirst()
              .orElse(null);
    }

    private void createVideos() {

        // reference video list
        videosYoga = new ArrayList<>();
        videosPilates = new ArrayList<>();

        // Video Category Labels
        yoga = labelRepo.save(new Label(Lang.EN, "video.category.yoga", "Yoga"));
        pilates = labelRepo.save(new Label(Lang.EN, "video.category.pilates", "Pilates"));
        flexibility = labelRepo.save(new Label(Lang.EN, "video.category.flexibility", "Flexibility"));

        // Video Tag Labels
        all = labelRepo.save(new Label(Lang.EN, "video.tag.all", "All"));
        relax = labelRepo.save(new Label(Lang.EN, "video.tag.relax", "Relax"));
        energy = labelRepo.save(new Label(Lang.EN, "video.tag.energy", "Energy"));
        strength = labelRepo.save(new Label(Lang.EN, "video.tag.strength", "Strength"));

        // Video Categories
        yogaCategory = createVideoCategory(EntityDefault.YOGA, yoga);
        videoCategoryRepo.save(yogaCategory);


        pilatesCategory = createVideoCategory(EntityDefault.PILATES, pilates);
        videoCategoryRepo.save(pilatesCategory);

        flexibilityCategory = createVideoCategory(EntityDefault.FLEXIBILITY, flexibility);
        flexibilityCategory.setArchived(archived);
        videoCategoryRepo.save(flexibilityCategory);

        // YOGA Videos
        yoga1En = createVideo(yogaCategory, "1010", "all, relax");
        videoRepo.save(yoga1En);
        videosYoga.add(yoga1En);

        yoga2En = createVideo(yogaCategory, "1020", "all, relax");
        videoRepo.save(yoga2En);
        videosYoga.add(yoga2En);

        // Pilates Videos
        pilates1En = createVideo(pilatesCategory, "2010", "all, energy, strength");
        videoRepo.save(pilates1En);
        videosPilates.add(pilates1En);

        pilates2En = createVideo(pilatesCategory, "2020", "all, energy, strength");
        videoRepo.save(pilates2En);
        videosPilates.add(pilates2En);

        pilates3En = createVideo(pilatesCategory, "2030", "all, energy, strength");
        videoRepo.save(pilates3En);
        videosPilates.add(pilates3En);

        pilates4En = createVideo(pilatesCategory, "2040", "all, energy, strength");
        videoRepo.save(pilates4En);
        videosPilates.add(pilates4En);

        pilates5En = createVideo(pilatesCategory, "2050", "all, energy, strength");
        videoRepo.save(pilates5En);
        videosPilates.add(pilates5En);
        
        pilatesFrench = createVideo(pilatesCategory, "2060", "all, energy, strength");
        pilatesFrench.setLanguage(Lang.FR); // FR
        videoRepo.save(pilatesFrench);
        videosPilates.add(pilatesFrench);

        pilatesArchived = createVideo(pilatesCategory, "2070", "all, energy, strength");
        pilatesArchived.setArchived(archived); // Archived
        videoRepo.save(pilatesArchived);
        videosPilates.add(pilatesArchived);

        // set references about the videos
        allVideos = videoRepo.findAll();
        activeAllEnIds = allVideos.stream().filter(v -> v.getLanguage() == Lang.EN && v.getArchived() == null).map(Video::getId).collect(Collectors.toSet());
        activeAllFrIds = allVideos.stream().filter(v -> v.getLanguage() == Lang.FR && v.getArchived() == null).map(Video::getId).collect(Collectors.toSet());
        activeYogaEnIds = allVideos.stream()
                .filter(v -> v.getLanguage() == Lang.EN && v.getArchived() == null && v.getVideoCategory().getCode().equals(yogaCategory.getCode()))
                .map(Video::getId).collect(Collectors.toSet());
        activePilatesEnIds = allVideos.stream()
              .filter(v -> v.getLanguage() == Lang.EN && v.getArchived() == null && v.getVideoCategory().getCode().equals(pilatesCategory.getCode()))
              .map(Video::getId).collect(Collectors.toSet());

    }

    private VideoCategory createVideoCategory(String code, Label label) {
        VideoCategory category = new VideoCategory();
        category.setCode(code);
        category.setTitle(NameUtils.toCamelCase(code));
        category.setLabel(label.getCode());
        category.setCreated(created);
        category.setReleased(released);
        category.setArchived(null);
        return category;
    }

    private Video createVideo(VideoCategory category, String number, String tags) {
        Video video = new Video();
        video.setVideoCategory(category);
        video.setCode("VIDEO_" + number);
        video.setTitle("Video #" + number);
        video.setDescription("Morning workout #" + number);
        video.setFilename(number + ".mov");
        video.setLanguage(Lang.EN);
        video.setPlayer(Player.PEECKO);
        video.setThumbnail("http://peecko/thumbnail/" + number);
        video.setUrl("http://peecko/video/" + number);
        video.setCreated(created);
        video.setReleased(released);
        video.setArchived(null);
        video.setDuration(Integer.valueOf(number));
        video.setTags(tags);
        return video;
    }

}
