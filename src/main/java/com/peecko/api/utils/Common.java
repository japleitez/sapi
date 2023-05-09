package com.peecko.api.utils;

import com.peecko.api.domain.Video;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Common {

    private static final int MAX = 10;
    private static final int MIN = 1;

    private static final Random RANDOM = new Random();

    public static int getRandomNum() {
        return RANDOM.nextInt((MAX - MIN) + 1) + MIN;
    }

    public static void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getVideoTags(List<Video> videos) {
        Set<String> set = new LinkedHashSet<>();
        for(Video v: videos) {
            set.addAll(v.getTags());
        }
        return set.stream().toList();
    }

    public static Video clone(Video v) {
        Video nv = new Video();
        nv.setCode(v.getCode());
        nv.setCategory(v.getCategory());
        nv.setTitle(v.getTitle());
        nv.setDuration(v.getDuration());
        nv.setCoach(v.getCoach());
        nv.setImage(v.getImage());
        nv.setUrl(v.getUrl());
        nv.setAudience(v.getAudience());
        nv.setIntensity(v.getIntensity());
        nv.setTags(v.getTags());
        nv.setDescription(v.getDescription());
        nv.setResume(v.getResume());
        nv.setPlayer(v.getPlayer());
        nv.setFavorite(v.isFavorite());
        return nv;
    }

}
