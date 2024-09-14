package com.peecko.api.service;

import com.peecko.api.domain.Label;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.LabelRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class LabelServiceTest {

    @Autowired
    private LabelService labelService;

    @Autowired
    private LabelRepo labelRepo;

    @Test
    void getCachedLabel() {
        //GIVEN
        Label label = new Label(Lang.EN, "key", "test");
        labelRepo.save(label);
        labelRepo.flush();

        //WHEN
        String result = labelService.getCachedLabel("key", Lang.EN);

        //THEN
        assertEquals("test", result);
    }

}