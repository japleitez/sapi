package com.peecko.api.service;

import com.peecko.api.repository.TodayVideoRepo;
import com.peecko.api.repository.UserFavoriteVideoRepo;
import com.peecko.api.repository.VideoCategoryRepo;
import com.peecko.api.repository.VideoRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VideoServiceTest {

    @Autowired
    VideoMapper videoMapper;

    @Autowired
    VideoRepo videoRepo;

    @Autowired
    LabelService labelService;

    @Autowired
    CacheManager cacheManager;

    @Autowired
    TodayVideoRepo todayVideoRepo;

    @Autowired
    VideoCategoryRepo videoCategoryRepo;

    @Autowired
    UserFavoriteVideoRepo userFavoriteVideoRepo;

    @Test
    void getCachedTodayVideos() {
    }

    @Test
    void getCachedLatestVideo() {
    }

    @Test
    void getCachedVideosByCategoryAndLang() {
    }

    @Test
    void getVideoTags() {
    }

    @Test
    void toVideoDTOs() {
    }

    @Test
    void resolveFavorites() {
    }

    @Test
    void testResolveFavorites() {
    }

    @Test
    void toCategoryDTOs() {
    }

    @Test
    void toCategoryDTO() {
    }

    @Test
    void addUserFavoriteVideo() {
    }

    @Test
    void removeUserFavoriteVideo() {
    }

    @Test
    void deleteFavoriteVideosForUser() {
    }

    @Test
    void findUserFavoriteVideos() {
    }

    @Test
    void existsByCode() {
    }
}