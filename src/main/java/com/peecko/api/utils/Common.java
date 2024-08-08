package com.peecko.api.utils;

import com.peecko.api.domain.dto.Device;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.web.payload.request.SignInRequest;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Common {

    private static final int MAX = 9;

    private static final int MIN = 0;

    public static final int MAX_ALLOWED = 3;

    public static final String OK = "OK";

    public static final String ERROR = "ERROR";

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        for(int i = 0; i < 10; i++) {
            System.out.println(generateDigit());
        }
    }

    public static int generateDigit() {
        return RANDOM.nextInt((MAX - MIN) + 1) + MIN;
    }

    public static String generatePinCode() {
        StringBuilder pin = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            pin.append(generateDigit());
        }
        return pin.toString();
    }

    public static void sleep(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<String> getVideoTags(List<VideoDTO> videos) {
        Set<String> set = new LinkedHashSet<>();
        for(VideoDTO v: videos) {
            set.addAll(v.getTags());
        }
        return set.stream().toList();
    }

    public static VideoDTO clone(VideoDTO v) {
        VideoDTO nv = new VideoDTO();
        nv.setCode(v.getCode());
        nv.setCategory(v.getCategory());
        nv.setTitle(v.getTitle());
        nv.setDuration(v.getDuration());
        nv.setCoach(v.getCoach());
        nv.setCoachWebsite(v.getCoachWebsite());
        nv.setCoachInstagram(v.getCoachInstagram());
        nv.setCoachEmail(v.getCoachEmail());
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

    public static Device mapToDevice(SignInRequest r) {
        Device d = new Device();
        d.setDeviceId(r.getDeviceId());
        d.setPhoneModel(r.getPhoneModel());
        d.setOsVersion(r.getOsVersion());
        return d;
    }

}
