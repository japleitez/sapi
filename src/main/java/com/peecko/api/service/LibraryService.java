package com.peecko.api.service;

import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.dto.CategoryDTO;
import com.peecko.api.domain.mapper.VideoCategoryMapper;
import com.peecko.api.repository.VideoCategoryRepo;
import com.peecko.api.repository.VideoRepo;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final VideoRepo videoRepo;
    private final FavoriteService favoriteService;
    private final VideoCategoryRepo categoryRepo;

    public LibraryService(VideoRepo videoRepo, FavoriteService favoriteService, VideoCategoryRepo categoryRepo) {
        this.videoRepo = videoRepo;
        this.favoriteService = favoriteService;
        this.categoryRepo = categoryRepo;
    }

    public List<CategoryDTO> getLibrary(Long userId) {
        List<VideoCategory> categories = categoryRepo.findByReleasedIsNotNullAndArchivedIsNull();
        List<Long> categoryIDs = categories.stream().map(VideoCategory::getId).collect(Collectors.toList());
        List<Video> videos = videoRepo.findTopByCategoriesOrderByUploadDateDesc(categoryIDs, 5 * categoryIDs.size());
        favoriteService.resolveUserFavoriteVideos(userId, videos);
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

}
