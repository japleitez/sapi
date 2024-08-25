package com.peecko.api.service;

import com.peecko.api.repository.VideoItemRepo;
import org.springframework.stereotype.Service;

@Service
public class VideoItemService {

    final VideoItemRepo videoItemRepo;

    public VideoItemService(VideoItemRepo videoItemRepo) {
        this.videoItemRepo = videoItemRepo;
    }
    public boolean existsByPlayListIdAndVideoCode(Long playlistId, String videoCode) {
        return videoItemRepo.existsByPlayListIdAndVideoCode(playlistId, videoCode);
    }
}
