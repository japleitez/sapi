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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VideoService {
    private final VideoRepo videoRepo;
    private final VideoCategoryRepo categoryRepo;
    private final UserFavoriteVideoRepo favoriteVideos;
    private final UserFavoriteVideoRepo userFavoriteVideoRepo;


    public VideoService(VideoRepo videoRepo, VideoCategoryRepo categoryRepo, UserFavoriteVideoRepo favoriteVideos, UserFavoriteVideoRepo userFavoriteVideoRepo) {
        this.videoRepo = videoRepo;
        this.categoryRepo = categoryRepo;
        this.favoriteVideos = favoriteVideos;
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

    public List<CategoryDTO> getLibrary(Long userId) {
        Instant today = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        Set<Long> favoriteIds = userFavoriteVideoRepo.findVideoIdsByApsUserId(userId);
        List<Long> categoryIDs = categoryRepo
                .findReleasedAsOfToday(today)
                .stream().toList();
        List<Video> videos = videoRepo
                .findTopByCategoriesOrderByUploadDateDesc(categoryIDs, 5 * categoryIDs.size())
                .stream()
                .map(v -> resolveFavorite(v, favoriteIds))
                .collect(Collectors.toList());
        return buildCategoryDTOs(videos);
    }

    private List<CategoryDTO> buildCategoryDTOs(List<Video> videos) {
        return videos.stream()
                .collect(Collectors.groupingBy(Video::getVideoCategory))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .sorted(Comparator.comparing(Video::getReleased).reversed())
                                .limit(5)
                                .collect(Collectors.toList())
                ))
                .entrySet()
                .stream()
                .map(VideoCategoryMapper::categoryDTO)
                .collect(Collectors.toList());
    }

    public List<VideoDTO> findUserFavoriteVideos(Long userId) {
        return favoriteVideos
                .findByApsUserId(userId)
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
