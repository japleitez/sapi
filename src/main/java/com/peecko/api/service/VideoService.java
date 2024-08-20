package com.peecko.api.service;

import com.peecko.api.domain.UserFavoriteVideo;
import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.mapper.VideoMapper;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import com.peecko.api.repository.VideoCategoryRepo;
import com.peecko.api.repository.VideoRepo;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoService {
    final VideoRepo videoRepo;
    final CacheManager cacheManager;
    final VideoCategoryRepo videoCategoryRepo;
    final UserFavoriteVideoRepo userFavoriteVideoRepo;

    public VideoService(VideoRepo videoRepo, CacheManager cacheManager, VideoCategoryRepo videoCategoryRepo, UserFavoriteVideoRepo userFavoriteVideoRepo) {
        this.videoRepo = videoRepo;
        this.cacheManager = cacheManager;
        this.videoCategoryRepo = videoCategoryRepo;
        this.userFavoriteVideoRepo = userFavoriteVideoRepo;
    }


    @Cacheable(value = "todayVideos")
    public List<Video> getTodayVideos() {
        return videoRepo.findAll();
    }

    @Cacheable(value = "videoLibrary")
    public Map<VideoCategory, List<Video>> getVideoLibrary() {
        Instant today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<VideoCategory> videoCategories = videoCategoryRepo.findReleasedAsOfToday(today);
        Map<VideoCategory, List<Video>> latestVideosByCategory = new HashMap<>();
        for (VideoCategory category : videoCategories) {
            List<Video> latestVideos = videoRepo.findTopReleasedAndNotArchived(category, today, PageRequest.of(0, 5));
            latestVideosByCategory.put(category, latestVideos);
        }
        return latestVideosByCategory;
    }

    @Cacheable(value = "videosByCategory", key = "#code + '-' + #language")
    public List<Video> getVideosByCategoryAndLang(String code, String language) {
        VideoCategory videoCategory = videoCategoryRepo.findByCode(code).orElse(null);
        if (videoCategory != null) {
            Instant today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
            return videoRepo.findReleasedAndNotArchived(videoCategory, today);
        }
        return new ArrayList<>();
    }

    public void addFavoriteVideo(Long apsUserId, String videoCode) {
        Video video = videoRepo.findByCode(videoCode).orElse(null);
        if (video == null) {
            return;
        }
        UserFavoriteVideo item = new UserFavoriteVideo();
        item.setVideo(video);
        item.setApsUserId(apsUserId);
        userFavoriteVideoRepo.save(item);
    }

    public void removeFavoriteVideo(Long apsUserId, String videoCode) {
        Video video = videoRepo.findByCode(videoCode).orElse(null);
        if (video != null) {
            userFavoriteVideoRepo.deleteByApsUserIdAndVideo(apsUserId, video);
        }
    }

    public void deleteFavoriteVideosForUser(Long apsUserId) {
        userFavoriteVideoRepo.deleteByApsUserId(apsUserId);
    }

    public List<VideoDTO> findUserFavoriteVideos(Long apsUserId) {
        return userFavoriteVideoRepo
                .findByApsUserIdOrderByIdDesc(apsUserId)
                .stream()
                .map(UserFavoriteVideo::getVideo)
                .map(VideoMapper::favoriteVideoDTO)
                .collect(Collectors.toList());
    }

}
