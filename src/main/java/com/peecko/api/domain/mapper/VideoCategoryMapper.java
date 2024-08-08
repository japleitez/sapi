package com.peecko.api.domain.mapper;

import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.dto.CategoryDTO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VideoCategoryMapper {

    public static CategoryDTO categoryDTO(Map.Entry<VideoCategory, List<Video>> entry) {
        return toCategoryDTO(entry.getKey(), entry.getValue());

    }
    public static CategoryDTO toCategoryDTO(VideoCategory videoCategory, List<Video> videos) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCode(videoCategory.getCode());
        dto.setTitle(videoCategory.getLabel()); //TODO translate label
        dto.setVideos(videos.stream().map(VideoMapper::toVideoDTO).collect(Collectors.toList()));
        return dto;
    }
}
