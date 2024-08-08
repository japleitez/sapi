package com.peecko.api.service;

import com.peecko.api.domain.UserFavoriteVideo;
import com.peecko.api.domain.UserFavoriteVideoID;
import com.peecko.api.domain.Video;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.mapper.VideoMapper;
import com.peecko.api.repository.UserFavoriteVideoIDRepo;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    private final UserFavoriteVideoRepo favoriteVideos;
    private final UserFavoriteVideoIDRepo favoriteVideoIDs;

    public FavoriteService(UserFavoriteVideoRepo favoriteVideos, UserFavoriteVideoIDRepo favoriteVideoIDs) {
        this.favoriteVideos = favoriteVideos;
        this.favoriteVideoIDs = favoriteVideoIDs;
    }

    public List<VideoDTO> findUserFavoriteVideos(Long userId) {
        List<UserFavoriteVideo> userFavoriteVideos = favoriteVideos.findByUserId(userId);
        List<VideoDTO> videoDTOs = userFavoriteVideos.stream().map(UserFavoriteVideo::getVideo).map(VideoMapper::toVideoDTO).collect(Collectors.toList());
        videoDTOs.forEach(videoDTO -> videoDTO.setFavorite(true));
        return videoDTOs;
    }

    public void resolveUserFavoriteVideos(Long userId, List<Video> videos) {
        List<UserFavoriteVideoID> videoIDS = favoriteVideoIDs.findByUserId(userId);
        Set<Long> favoriteIds = videoIDS.stream().map(UserFavoriteVideoID::getVideoId).collect(Collectors.toSet());
        videos.forEach(video -> video.setFavorite(favoriteIds.contains(video.getId())));
    }

}
