package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.CategoryDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import com.peecko.api.repository.VideoCategoryRepo;
import com.peecko.api.repository.VideoRepo;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VideoService {

    final VideoMapper videoMapper;
    final VideoRepo videoRepo;
    final LabelService labelService;
    final CacheManager cacheManager;
    final VideoCategoryRepo videoCategoryRepo;
    final UserFavoriteVideoRepo userFavoriteVideoRepo;
    public static final int CATEGORY_VIDEOS_MAX_SIZE = 4;

    List<String> todayCategoryCodes = List.of("fc.full.body");

    public VideoService(VideoMapper videoMapper, VideoRepo videoRepo, LabelService labelService, CacheManager cacheManager, VideoCategoryRepo videoCategoryRepo, UserFavoriteVideoRepo userFavoriteVideoRepo) {
        this.videoMapper = videoMapper;
        this.videoRepo = videoRepo;
        this.labelService = labelService;
        this.cacheManager = cacheManager;
        this.videoCategoryRepo = videoCategoryRepo;
        this.userFavoriteVideoRepo = userFavoriteVideoRepo;
    }
    public void clearAllCaches() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    @Cacheable(value = "todayVideos", key = "#lang")
    public List<Video> getCachedTodayVideos(Lang lang) {
        Set<Long> videoIds = new HashSet<>();
        for (String code : todayCategoryCodes) {
            videoCategoryRepo.findByCode(code)
                    .map(category -> getCachedVideosByCategoryAndLang(category, lang))
                    .filter(videos -> !videos.isEmpty())
                    .map(videos -> videos.get(ThreadLocalRandom.current().nextInt(videos.size())))
                    .ifPresent(video -> videoIds.add(video.getId()));
        }
        if (videoIds.isEmpty()) {
            return List.of();
        }
        return videoRepo.findByIdIn(videoIds);
    }

    @Cacheable(value = "videoLibrary", key = "#lang")
    public Map<VideoCategory, List<Video>> getCachedLatestVideo(Lang lang) {
        LocalDate today = LocalDate.now();
        List<VideoCategory> categories = videoCategoryRepo.findReleasedCategories(today);
        Map<VideoCategory, List<Video>> videoCategoryMap = new LinkedHashMap<>();
        for (VideoCategory category : categories) {
            List<Video> latestVideos = videoRepo.findByCategoryAndLang(category, lang, today);
            if (!latestVideos.isEmpty()) {
                int endIndex = Math.min(3, latestVideos.size());
                List<Video> topVideos = new ArrayList<>(latestVideos.subList(0, endIndex));
                videoCategoryMap.put(category, topVideos);
            }
        }
        return videoCategoryMap;
    }

    @Cacheable(value = "videosByCategory", key = "{#videoCategory.code(), #lang}")
    public List<Video> getCachedVideosByCategoryAndLang(VideoCategory videoCategory, Lang lang) {
        return videoRepo.findByCategoryAndLang(videoCategory, lang, LocalDate.now());
    }

    public List<String> getVideoTags(List<VideoDTO> videos, Lang lang) {
        if (videos.isEmpty() || lang == null) {
            return Collections.emptyList();
        }
        return videos.stream()
                .filter(Objects::nonNull) // filter null videos
                .map(VideoDTO::getTags)
                .filter(Objects::nonNull) // filter null tag lists
                .flatMap(Collection::stream)
                .filter(Objects::nonNull) // filter null tags
                .distinct()
                .map(tag -> labelService.getCachedVideoTagLabel(tag, lang))
                .filter(Objects::nonNull) // filter null labels
                .sorted()
                .toList();
    }

    public List<VideoDTO> toVideoDTOs(List<Video> videos, Lang lang) {
        return videos.stream().map(v -> videoMapper.toVideoDTO(v, lang)).toList();
    }

    public void resolveFavorites(List<Video> videos, Long userId) {
        Set<Long> favorites = userFavoriteVideoRepo.findVideoIdsByApsUserId(userId);
        if (favorites.isEmpty()) {
            videos.forEach(video -> video.setFavorite(false));
        } else {
            videos.forEach(video -> video.setFavorite(favorites.contains(video.getId())));
        }
    }

    public void resolveFavorites(Map<VideoCategory, List<Video>> categoryVideos, Long userId) {
        Set<Long> favoriteIds = userFavoriteVideoRepo.findVideoIdsByApsUserId(userId);
        if (favoriteIds.isEmpty()) {
            categoryVideos.forEach((key, value) -> value.forEach(video -> video.setFavorite(false)));
        } else {
            categoryVideos.forEach((key, value) -> value.forEach(video -> video.setFavorite(favoriteIds.contains(video.getId()))));
        }
    }

    public List<CategoryDTO> toCategoryDTOs(Map<VideoCategory, List<Video>> categoryVideos, Lang lang) {
        return categoryVideos.entrySet().stream()
                .map(entry -> videoMapper.toCategoryDTO(entry.getKey(), entry.getValue(), lang)).sorted(Comparator.comparing(CategoryDTO::getPos)).toList();
    }

    public CategoryDTO toCategoryDTO(VideoCategory category, List<Video> videos, Lang lang) {
        return videoMapper.toCategoryDTO(category, videos, lang);
    }

    public void addUserFavoriteVideo(Long apsUserId, String videoCode) {
        Video video = videoRepo.findByCode(videoCode).orElse(null);
        if (video == null) {
            return;
        }
        UserFavoriteVideo item = new UserFavoriteVideo();
        item.setVideo(video);
        item.setApsUserId(apsUserId);
        userFavoriteVideoRepo.save(item);
    }

    public void removeUserFavoriteVideo(Long apsUserId, String videoCode) {
        videoRepo.findByCode(videoCode).ifPresent(video -> userFavoriteVideoRepo.deleteByApsUserIdAndVideo(apsUserId, video));
    }

    public void deleteFavoriteVideosForUser(Long apsUserId) {
        userFavoriteVideoRepo.deleteByApsUserId(apsUserId);
    }

    public List<Video> findUserFavoriteVideos(Long apsUserId) {
        return userFavoriteVideoRepo
                .findByApsUserIdOrderByIdDesc(apsUserId)
                .stream()
                .map(UserFavoriteVideo::getVideo)
                .map(this::videoFavorite).toList();
    }

    private Video videoFavorite(Video video) {
        video.setFavorite(true);
        return video;
    }

    public boolean existsByCode(String videoCode) {
        return videoRepo.existsByCode(videoCode);
    }

}
