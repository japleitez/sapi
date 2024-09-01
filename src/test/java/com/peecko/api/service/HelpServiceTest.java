package com.peecko.api.service;

import com.peecko.api.domain.HelpItem;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.HelpItemRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HelpServiceTest {

    @Autowired
    HelpService helpService;

    @Autowired
    HelpItemRepo helpItemRepo;

    @Test
    void findByLang() {
        //GIVEN
        helpItemRepo.save(new HelpItem(Lang.EN, "question", "answer"));
        helpItemRepo.save(new HelpItem(Lang.FR, "question", "answer"));
        helpItemRepo.save(new HelpItem(Lang.FR, "question", "answer"));
        helpItemRepo.save(new HelpItem(Lang.DE, "question", "answer"));
        helpItemRepo.save(new HelpItem(Lang.DE, "question", "answer"));
        helpItemRepo.save(new HelpItem(Lang.DE, "question", "answer"));
        helpItemRepo.flush();

        //WHEN
        int sizeEN = helpService.findByLang(Lang.EN).size();
        int sizeFR = helpService.findByLang(Lang.FR).size();
        int sizeDE = helpService.findByLang(Lang.DE).size();

        //THEN
        assertEquals(1, sizeEN);
        assertEquals(2, sizeFR);
        assertEquals(3, sizeDE);

    }
}