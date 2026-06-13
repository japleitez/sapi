package com.peecko.api.service;

import com.peecko.api.domain.dto.Help;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.mapper.HelpItemMapper;
import com.peecko.api.repository.HelpItemRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelpService {

    final HelpItemRepo helpItemRepo;

    public HelpService(HelpItemRepo helpItemRepo) {
        this.helpItemRepo = helpItemRepo;
    }

    public List<Help> findByLang(Lang lang) {
        return helpItemRepo.findByLang(lang).stream().map(HelpItemMapper::help).toList();
    }

}
