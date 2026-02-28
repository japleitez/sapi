package com.peecko.api.service;

import com.peecko.api.domain.Video;
import com.peecko.api.repository.VideoItemRepo;
import com.peecko.api.repository.VideoRepo;
import org.springframework.stereotype.Service;

@Service
public class VideoItemService {

    final VideoItemRepo videoItemRepo;

    public VideoItemService(VideoRepo videoRepo,  VideoItemRepo videoItemRepo) {
        this.videoItemRepo = videoItemRepo;
    }

    public boolean existsByPlayListIdAndCode(Long playlistId, String code) {
        return videoItemRepo.existsVideoItem(playlistId, code);
    }

}
