package com.peecko.api.service;

import com.peecko.api.domain.UserFavoriteVideo;
import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.dto.CategoryDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.mapper.VideoCategoryMapper;
import com.peecko.api.domain.mapper.VideoMapper;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import com.peecko.api.repository.VideoCategoryRepo;
import com.peecko.api.repository.VideoRepo;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoService {
    private final VideoRepo videoRepo;
    private final VideoCategoryRepo videoCategoryRepo;
    private final UserFavoriteVideoRepo userFavoriteVideoRepo;

    private final int MAX_VIDEOS_BY_CATEGORY = 5;

    public VideoService(VideoRepo videoRepo, VideoCategoryRepo videoCategoryRepo, UserFavoriteVideoRepo userFavoriteVideoRepo) {
        this.videoRepo = videoRepo;
        this.videoCategoryRepo = videoCategoryRepo;
        this.userFavoriteVideoRepo = userFavoriteVideoRepo;
    }

    public List<VideoDTO> getTodayVideos(Long apsUserId) {
        Set<Long> favoriteIds = userFavoriteVideoRepo.findVideoIdsByApsUserId(apsUserId);
        return videoRepo
                .findAll()
                .stream()
                .map(v -> resolveFavorite(v, favoriteIds))
                .map(VideoMapper::videoDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getLibrary(Long apsUserId) {
        Instant today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Set<Long> favoriteIds = userFavoriteVideoRepo.findVideoIdsByApsUserId(apsUserId);
        List<Long> categoryIDs = videoCategoryRepo.findReleasedAsOfToday(today).stream().toList();
        int limit  = MAX_VIDEOS_BY_CATEGORY * categoryIDs.size();
        return  videoRepo
                .findTopByCategoriesOrderByUploadDateDesc(categoryIDs, limit)
                .stream()
                .map(v -> resolveFavorite(v, favoriteIds))
                .collect(Collectors.groupingBy(Video::getVideoCategory))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(Video::getReleased).reversed())
                                .limit(MAX_VIDEOS_BY_CATEGORY)
                                .collect(Collectors.toList())
                ))
                .entrySet()
                .stream()
                .map(VideoCategoryMapper::categoryDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategory(Long apsUserId, String categoryCode) {
        VideoCategory videoCategory = videoCategoryRepo.findByCode(categoryCode).orElse(null);
        if (videoCategory == null) {
            return null;
        }
        Set<Long> favoriteIds = userFavoriteVideoRepo.findVideoIdsByApsUserId(apsUserId);
        Instant today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<Video> videos = videoRepo
                .findReleasedAndNotArchivedVideos(videoCategory, today)
                .stream()
                .map(v -> resolveFavorite(v, favoriteIds))
                .toList();
        return VideoCategoryMapper.categoryDTO(videoCategory, videos);
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


    private Video resolveFavorite(Video video, Set<Long> favoriteIds) {
        video.setFavorite(favoriteIds.contains(video.getId()));
        return video;
    }

}
