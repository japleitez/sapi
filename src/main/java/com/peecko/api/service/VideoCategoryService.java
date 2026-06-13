package com.peecko.api.service;

import com.peecko.api.domain.VideoCategory;
import com.peecko.api.repository.VideoCategoryRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VideoCategoryService {

    final VideoCategoryRepo videoCategoryRepo;

    public VideoCategoryService(VideoCategoryRepo videoCategoryRepo) {
        this.videoCategoryRepo = videoCategoryRepo;
    }

    public Optional<VideoCategory> findByCode(String code) {
        return videoCategoryRepo.findByCode(code);
    }

}
