package com.peecko.api.domain.mapper;

import com.peecko.api.domain.Video;
import com.peecko.api.domain.dto.VideoDTO;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VideoMapper {
    public static VideoDTO videoDTO(Video video) {
        if (video == null) {
            return null;
        }
        VideoDTO dto  = new VideoDTO();
        dto.setCode(video.getCode());
        dto.setCategory(video.getVideoCategory().getCode());
        dto.setTitle(video.getTitle());
        dto.setDuration(String.valueOf(video.getDuration()));
        dto.setCoach(video.getCoach().getName());
        dto.setImage(video.getThumbnail());
        dto.setUrl(video.getUrl());
        dto.setAudience(video.getAudience()); //TODO translate label (e.g. women)
        dto.setIntensity(video.getIntensity().name()); // TODO, translate label (e.g. intermediate)
        dto.setTags(convertToList(video.getTags())); //TODO, translate label (e.g. energy, endurance)
        dto.setDescription(video.getDescription());
        dto.setResume(video.getCoach().getResume());
        dto.setPlayer(video.getPlayer().name());
        dto.setFavorite(video.isFavorite());
        dto.setCoachWebsite(video.getCoach().getWebsite());
        dto.setCoachEmail(video.getCoach().getEmail());
        dto.setCoachInstagram(video.getCoach().getInstagram());
        return dto;
    }

    private static List<String> convertToList(String values) {
        return StringUtils.hasText(values)? Arrays.asList(values.split(",")) : new ArrayList<>();
    }
}
