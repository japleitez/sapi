package com.peecko.api.service;

import com.peecko.api.domain.Coach;
import com.peecko.api.domain.Video;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.dto.CategoryDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.enumeration.Lang;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoMapper {

    final LabelService labelService;

    public VideoMapper(LabelService labelService) {
        this.labelService = labelService;
    }

    public CategoryDTO toCategoryDTO(VideoCategory videoCategory, List<Video> videos, Lang lang) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCode(videoCategory.getCode());
        dto.setTitle(labelService.getCachedLabel("video.category." + videoCategory.getLabel(), lang));
        if (videos != null && !videos.isEmpty()) {
            dto.setVideos(videos.stream().map(video -> toVideoDTO(video, lang)).collect(Collectors.toList()));
        }
        return dto;
    }

    public VideoDTO toVideoDTO(Video video, Lang lang) {
        VideoDTO dto  = new VideoDTO();
        dto.setCode(video.getCode());
        dto.setCategory(video.getVideoCategory().getCode());
        dto.setTitle(video.getTitle());
        dto.setDuration(String.valueOf(video.getDuration()));
        dto.setImage(video.getThumbnail());
        dto.setUrl(video.getUrl());
        dto.setDescription(video.getDescription());
        dto.setPlayer(video.getPlayer().name());
        dto.setFavorite(video.isFavorite());
        if (StringUtils.hasText(video.getAudience())) {
            dto.setAudience(labelService.getCachedLabel("video.audience." + video.getAudience().toLowerCase(), lang));
        }
        if (video.getIntensity() != null) {
            dto.setIntensity(labelService.getCachedLabel("video.intensity." + video.getIntensity().name().toLowerCase(), lang));
        }
        if (StringUtils.hasText(video.getTags())) {
            dto.setTags(buildVideoTagsAsLabelList(video.getTags(), lang));
        }
        if (video.getCoach() != null) {
            Coach coach = video.getCoach();
            dto.setCoach(coach.getName());
            dto.setResume(coach.getResume());
            dto.setCoachWebsite(coach.getWebsite());
            dto.setCoachEmail(coach.getEmail());
            dto.setCoachInstagram(coach.getInstagram());
        }
        return dto;
    }

    private List<String> buildVideoTagsAsLabelList(String codes, Lang lang) {
        List<String> list = new ArrayList<>();
        String[] array = codes.split(",");
        for(String code: array) {
            code = code.trim();
            if (StringUtils.hasText(code)) {
                list.add(labelService.getCachedLabel("video.tag." + code, lang));
            }
        }
        return list;
    }

}
