package com.peecko.api.domain.mapper;

import com.peecko.api.domain.PlayList;
import com.peecko.api.domain.dto.IdName;
import com.peecko.api.domain.dto.PlaylistDTO;

public class PlayListMapper {

    public static IdName idName(PlayList playList) {
        return new IdName(playList.getId(), playList.getName(), playList.getCounter());
    }

    public static PlaylistDTO playlistDTO(PlayList playList) {
        PlaylistDTO dto =  new PlaylistDTO();
        dto.setId(playList.getId());
        dto.setName(playList.getName());
        return dto;
    }

}
