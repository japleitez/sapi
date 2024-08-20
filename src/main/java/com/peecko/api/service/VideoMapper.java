package com.peecko.api.service;

import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.dto.CategoryDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VideoMapper {

    final LabelService labelService;

    public VideoMapper(LabelService labelService) {
        this.labelService = labelService;
    }

    public VideoDTO videoDTO(Video video) {
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
        dto.setAudience(labelService.getLabel(video.getAudience(), Lang.EN));
        dto.setIntensity(labelService.getLabel(video.getIntensity().name(), Lang.EN));
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

    private List<String> getLabelsAsList(String codes, Lang lang) {
        if (StringUtils.hasText(codes)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        String[] codeArray = codes.split(",");
        for(String code: codeArray) {
            list.add(labelService.getLabel(code, lang));
        }
        return list;
    }

    public VideoDTO favoriteVideoDTO(Video video) {
        VideoDTO dto = videoDTO(video);
        dto.setFavorite(true);
        return dto;
    }

    private List<String> convertToList(String values) {
        return StringUtils.hasText(values)? Arrays.asList(values.split(",")) : new ArrayList<>();
    }

    public CategoryDTO categoryDTO(Map.Entry<VideoCategory, List<Video>> entry) {
        return categoryDTO(entry.getKey(), entry.getValue());

    }

    public CategoryDTO categoryDTO(VideoCategory videoCategory, List<Video> videos) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCode(videoCategory.getCode());
        dto.setTitle(labelService.getLabel(videoCategory.getLabel(), Lang.EN));
        dto.setVideos(videos.stream().map(this::videoDTO).collect(Collectors.toList()));
        return dto;
    }

}
