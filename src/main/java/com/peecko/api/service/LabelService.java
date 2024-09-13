package com.peecko.api.service;

import com.peecko.api.domain.Label;
import com.peecko.api.domain.enumeration.Intensity;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.LabelRepo;
import com.peecko.api.utils.TagUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    final LabelRepo labelRepo;
    public static final String PREFIX_VIDEO_TAG = "video.tag.";
    public static final String PREFIX_VIDEO_CATEGORY = "video.category.";
    public static final String PREFIX_VIDEO_AUDIENCE = "video.audience.";
    public static final String PREFIX_VIDEO_INTENSITY = "video.intensity.";

    public LabelService(LabelRepo labelRepo) {
        this.labelRepo = labelRepo;
    }

    @Cacheable(value = "labels", key = "#code + '-' + #lang.name()")
    public String getCachedLabel(String code, Lang lang) {
        return labelRepo.findByCodeAndLang(code, lang).map(Label::getText).orElse(code);
    }

    public static String resolveAudienceLabel(@NotNull String audience) {
        return LabelService.PREFIX_VIDEO_AUDIENCE + audience.toLowerCase();
    }

    public static String resolveIntensityLabel(@NotNull Intensity intensity) {
        return LabelService.PREFIX_VIDEO_INTENSITY + intensity.name().toLowerCase();
    }

    public static String resolveVideoTagLabel(@NotNull String tag) {
        return LabelService.PREFIX_VIDEO_TAG + tag.toLowerCase();
    }

    public static List<String> resolveVideoTagsAsLabelList(@NotNull String tags) {
        return TagUtils.convertToList(tags)
              .stream()
              .map(LabelService::resolveVideoTagLabel)
              .toList();
    }
}
