package com.peecko.api.domain.mapper;

import com.peecko.api.domain.PlayList;
import com.peecko.api.domain.dto.IdName;
import com.peecko.api.domain.dto.PlayListDTO;

public class PlayListMapper {

    public static IdName toIdName(PlayList playList) {
        return new IdName(playList.getId(), playList.getName(), playList.getCounter());
    }

    public static PlayListDTO toPlayListDTO(PlayList playList) {
        PlayListDTO dto =  new PlayListDTO();
        dto.setId(playList.getId());
        dto.setName(playList.getName());
        return dto;
    }

}
