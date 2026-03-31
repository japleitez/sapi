package com.peecko.api.service;

import com.peecko.api.repository.PlayListItemRepo;
import org.springframework.stereotype.Service;

@Service
public class PlayListItemService {

    final PlayListItemRepo playListItemRepo;

    public PlayListItemService(PlayListItemRepo playListItemRepo) {
        this.playListItemRepo = playListItemRepo;
    }

    public boolean existsByPlayListIdAndCode(Long playlistId, String code) {
        return playListItemRepo.existsVideoItem(playlistId, code);
    }

}
