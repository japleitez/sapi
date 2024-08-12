package com.peecko.api.service;

import com.peecko.api.domain.Video;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.mapper.VideoMapper;
import com.peecko.api.repository.VideoRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodayService {
    private final VideoRepo videoRepo;

    private final FavoriteService favoriteService;
    public TodayService(VideoRepo videoRepo, FavoriteService favoriteService) {
        this.videoRepo = videoRepo;
        this.favoriteService = favoriteService;
    }

    public List<VideoDTO> getTodayVideos(Long userId) {
        List<Video> videos = videoRepo.findAll();
        favoriteService.resolveUserFavoriteVideos(userId, videos);
        return videos.stream().map(VideoMapper::videoDTO).collect(Collectors.toList());
    }

}
