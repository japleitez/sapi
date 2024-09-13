package com.peecko.api.service;

import com.peecko.api.domain.dto.LanguageDTO;
import com.peecko.api.domain.mapper.LanguageMapper;
import com.peecko.api.repository.LanguageRepo;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LanguageService {

    final LanguageRepo languageRepo;

    public LanguageService(LanguageRepo languageRepo) {
        this.languageRepo = languageRepo;
    }

    public List<LanguageDTO> findActiveLanguages() {
        return languageRepo.findByActiveTrueOrderByName().stream().map(LanguageMapper::languageDTO).toList();
    }

}
