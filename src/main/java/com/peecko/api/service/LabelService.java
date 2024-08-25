package com.peecko.api.service;

import com.peecko.api.domain.Label;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.LabelRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class LabelService {

    final LabelRepo labelRepo;

    public LabelService(LabelRepo labelRepo) {
        this.labelRepo = labelRepo;
    }

    @Cacheable(value = "labels", key = "#code + '-' + #lang.name()")
    public String getCachedLabel(String code, Lang lang) {
        return labelRepo.findByCodeAndLang(code, lang).map(Label::getText).orElse(code);
    }

}
