package com.peecko.api.utils;

import com.peecko.api.domain.ApsDevice;
import com.peecko.api.domain.dto.DeviceDTO;
import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.web.payload.request.SignInRequest;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.time.temporal.TemporalAdjusters;
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
        Set<String> tags = new LinkedHashSet<>();
        videos.forEach(videoDTO -> tags.addAll(videoDTO.getTags()));
        return tags.stream().sorted().toList();
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

    static final DateTimeFormatter CUSTOM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String instantAsString(Instant time) {
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.ofInstant(time, zoneId);
        return localDateTime.format(formatter);
    }

    public static String localDateAsString(LocalDate localDate) {
        LocalDateTime localDateTime = localDate.atTime(LocalTime.MIDNIGHT);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

    public static DeviceDTO mapToDevice(SignInRequest r) {
        DeviceDTO d = new DeviceDTO();
        d.setDeviceId(r.getDeviceId());
        d.setPhoneModel(r.getPhoneModel());
        d.setOsVersion(r.getOsVersion());
        return d;
    }

    public static ApsDevice toApsDevice(SignInRequest request) {
        ApsDevice apsDevice = new ApsDevice();
        apsDevice.username(request.getUsername());
        apsDevice.deviceId(request.getDeviceId());
        apsDevice.osVersion(request.getOsVersion());
        apsDevice.phoneModel(request.getPhoneModel());
        apsDevice.installedOn(Instant.now());
        return apsDevice;
    }


    public static int currentYearMonth() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String yearMonthString = LocalDate.now().format(formatter);
        return Integer.parseInt(yearMonthString);
    }

    public static String lastDayOfMonthAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate lastDayOfMonth = lastDayOfMonth();
        return lastDayOfMonth.format(formatter);
    }

    public static LocalDate lastDayOfMonth() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static Lang toLanguage(String lang) {
        if (lang == null) {
            return Lang.EN;
        }
        try {
            return Lang.valueOf(lang.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Lang.EN;
        }
    }

    public static String generateRandomDigitString(int length) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10); // Generates a random digit between 0 and 9
            stringBuilder.append(digit);
        }

        return stringBuilder.toString();
    }

}
