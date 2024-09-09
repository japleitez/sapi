package com.peecko.api.service;

import com.peecko.api.domain.EntityBuilder;
import com.peecko.api.domain.EntityDefault;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.repository.VideoCategoryRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VideoCategoryServiceTest {

    @Autowired
    VideoCategoryRepo videoCategoryRepo;

    @Autowired
    VideoCategoryService videoCategoryService;

    @Test
    void findByCode() {
        // Given
        VideoCategory videoCategory = EntityBuilder.buildVideoCategory();
        videoCategoryRepo.save(videoCategory);

        // When
        Optional<VideoCategory> actual = videoCategoryService.findByCode(EntityDefault.VIDEO_CATEGORY_CODE);

        // Then
        assertTrue(actual.isPresent());
        assertEquals(videoCategory, actual.get());
    }
}
