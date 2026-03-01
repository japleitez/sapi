package com.peecko.api.service;

import com.peecko.api.domain.Label;
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


    public static final String FC_TAG = "fc.tag.";

    public static final String INTENSITY = "intensity.";

    public static final String AUDIENCE = "audience.";

    public LabelService(LabelRepo labelRepo) {
        this.labelRepo = labelRepo;
    }

    @Cacheable(value = "labels", key = "{#code, #lang}")
    public String getCachedLabel(String code, Lang lang) {
        return labelRepo.findByCodeAndLang(code, lang).map(Label::getText).orElse(code);
    }

    @Cacheable(value = "videoTagLabels", key = "{#code, #lang}")
    public String getCachedVideoTagLabel(String code, Lang lang) {
        return labelRepo.findByCodeAndLang(FC_TAG + code, lang).map(Label::getText).orElse(code);
    }

    @Cacheable(value = "intensityLabels", key = "{#code, #lang}")
    public String getCachedIntensityLabel(String code, Lang lang) {
        return labelRepo.findByCodeAndLang(INTENSITY + code, lang).map(Label::getText).orElse(code);
    }

    @Cacheable(value = "audienceLabels", key = "{#code, #lang}")
    public String getCachedAudienceLabel(String code, Lang lang) {
        return labelRepo.findByCodeAndLang(AUDIENCE + code, lang).map(Label::getText).orElse(code);
    }

    public static String resolveVideoTagLabel(@NotNull String tag) {
        return LabelService.FC_TAG + tag.toLowerCase();
    }

    public static List<String> resolveVideoTagsAsLabelList(@NotNull String tags) {
        return TagUtils.convertToList(tags)
              .stream()
              .map(LabelService::resolveVideoTagLabel)
              .toList();
    }
}
