package com.peecko.api.service;

import com.peecko.api.domain.UserFavoriteVideo;
import com.peecko.api.domain.Video;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.mapper.VideoMapper;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final UserFavoriteVideoRepo favoriteVideos;

    public FavoriteService(UserFavoriteVideoRepo favoriteVideos) {
        this.favoriteVideos = favoriteVideos;
    }

    public List<VideoDTO> findUserFavoriteVideos(Long userId) {
        List<UserFavoriteVideo> userFavoriteVideos = favoriteVideos.findByApsUserId(userId);
        List<VideoDTO> videoDTOs = userFavoriteVideos.stream().map(UserFavoriteVideo::getVideo).map(VideoMapper::videoDTO).collect(Collectors.toList());
        videoDTOs.forEach(videoDTO -> videoDTO.setFavorite(true));
        return videoDTOs;
    }

    public void resolveUserFavoriteVideos(Long apsUserId, List<Video> videos) {
        Set<Long> favoriteIds = favoriteVideos.findVideoIdsByApsUserId(apsUserId);
        videos.forEach(video -> video.setFavorite(favoriteIds.contains(video.getId())));
    }

}
