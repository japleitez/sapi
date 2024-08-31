package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.CategoryDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.TodayVideoRepo;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import com.peecko.api.repository.VideoCategoryRepo;
import com.peecko.api.repository.VideoRepo;
import com.peecko.api.utils.Common;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoService {

    final VideoMapper videoMapper;
    final VideoRepo videoRepo;
    final LabelService labelService;
    final CacheManager cacheManager;
    final TodayVideoRepo todayVideoRepo;
    final VideoCategoryRepo videoCategoryRepo;
    final UserFavoriteVideoRepo userFavoriteVideoRepo;

    final static int CATEGORY_VIDEOS_SIZE = 4;

    public VideoService(VideoMapper videoMapper, VideoRepo videoRepo, LabelService labelService, CacheManager cacheManager, TodayVideoRepo todayVideoRepo, VideoCategoryRepo videoCategoryRepo, UserFavoriteVideoRepo userFavoriteVideoRepo) {
        this.videoMapper = videoMapper;
        this.videoRepo = videoRepo;
        this.labelService = labelService;
        this.cacheManager = cacheManager;
        this.todayVideoRepo = todayVideoRepo;
        this.videoCategoryRepo = videoCategoryRepo;
        this.userFavoriteVideoRepo = userFavoriteVideoRepo;
    }

    @Cacheable(value = "todayVideos", key = "#lang.name()")
    public List<Video> getCachedTodayVideos(Lang lang) {
        TodayVideo latestTodayVideo = todayVideoRepo.findFirstByLanguageOrderByReleaseDateDesc(lang);
        if (latestTodayVideo != null) {
            return videoRepo.findByIdIn(latestTodayVideo.getVideoIds());
        }
        return List.of();
    }

    @Cacheable(value = "videoLibrary", key = "#lang.name()")
    public Map<VideoCategory, List<Video>> getCachedLatestVideo(Lang lang) {
        Instant today = Common.endOfDay();
        List<VideoCategory> categories = videoCategoryRepo.findReleasedCategories(today);
        Map<VideoCategory, List<Video>> videoCategoryMap = new HashMap<>();
        for (VideoCategory category : categories) {
            List<Video> latestVideos = videoRepo.findLatestByCategoryAndLang(category, lang, today, PageRequest.of(0, CATEGORY_VIDEOS_SIZE));
            videoCategoryMap.put(category, latestVideos);
        }
        return videoCategoryMap;
    }

    @Cacheable(value = "videosByCategory", key = "#videoCategory.code + '-' + #lang.name()")
    public List<Video> getCachedVideosByCategoryAndLang(VideoCategory videoCategory, Lang lang) {
        Instant today = Common.endOfDay();
        return videoRepo.findByCategoryAndLang(videoCategory, lang, today);
    }

    public List<String> getVideoTags(List<VideoDTO> videos, Lang lang) {
        return videos.stream()
                .map(VideoDTO::getTags)
                .flatMap(Collection::stream)
                .distinct()
                .map(tag -> labelService.getCachedLabel(tag, lang))
                .sorted()
                .collect(Collectors.toList());
    }

    public List<VideoDTO> toVideoDTOs(List<Video> videos, Lang lang) {
        return videos.stream().map(v -> videoMapper.toVideoDTO(v, lang)).collect(Collectors.toList());
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
                .map(entry -> videoMapper.toCategoryDTO(entry.getKey(), entry.getValue(), lang))
                .collect(Collectors.toList());

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
                .map(this::videoFavorite)
                .collect(Collectors.toList());
    }

    private Video videoFavorite(Video video) {
        video.setFavorite(true);
        return video;
    }

    public boolean existsByCode(String videoCode) {
        return videoRepo.existsByCode(videoCode);
    }

}