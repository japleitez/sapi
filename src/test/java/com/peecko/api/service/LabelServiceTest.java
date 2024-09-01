package com.peecko.api.service;

import com.peecko.api.domain.Label;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.LabelRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LabelServiceTest {

    @Autowired
    private LabelService labelService;

    @Autowired
    private LabelRepo labelRepo;

    @BeforeEach
    void beforeEach() {
        labelRepo.deleteAll();
        labelRepo.flush();
    }

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