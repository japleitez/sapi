package com.peecko.api.service;

import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.VideoFavorite;
import com.peecko.api.domain.dto.CategoryDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.mapper.VideoCategoryMapper;
import com.peecko.api.domain.mapper.VideoMapper;
import com.peecko.api.repository.VideoFavoriteRepo;
import com.peecko.api.repository.VideoRepo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoService {
    private final VideoRepo videoRepo;
    private final VideoFavoriteRepo videoFavoriteRepo;

    public VideoService(VideoRepo videoRepo, VideoFavoriteRepo videoFavoriteRepo) {
        this.videoRepo = videoRepo;
        this.videoFavoriteRepo = videoFavoriteRepo;
    }

    public List<VideoDTO> getTodayVideos(Long userId) {
        List<Video> videos = videoRepo.findAll(); //TODO videos for today's need a flag (perhaps a cron job that make the selection automatically)
        resolveUserFavoriteVideos(userId, videos);
        return videos.stream().map(VideoMapper::toVideoDTO).collect(Collectors.toList());
    }

    public List<CategoryDTO> getLibrary(Long userId) {
        List<Long> categories = new ArrayList<>();
        categories.add(1L);
        //TODO we can cache the result of this query for a day and have a dirty flag to refresh it on demand
        List<Video> videos = videoRepo.findTopByCategoriesOrderByUploadDateDesc(categories, 5 * categories.size());
        resolveUserFavoriteVideos(userId, videos);
        return buildCategoryDTOs(videos);
    }

    private void resolveUserFavoriteVideos(Long userId, List<Video> videos) {
        List<VideoFavorite> favorites = videoFavoriteRepo.findByUserId(userId);
        Set<Long> favoriteIds = favorites.stream().map(VideoFavorite::getVideoId).collect(Collectors.toSet());
        videos.forEach(video -> video.setFavorite(favoriteIds.contains(video.getId())));
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

}
