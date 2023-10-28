package com.peecko.api.utils;

import com.peecko.api.domain.Coach;
import com.peecko.api.domain.Video;
import com.peecko.api.repository.CoachRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VideoLoader {

    private static final String SEMICOLON = ";";
    private static final String YOUTUBE = "youtube";
    private static final String PEECKO = "peecko";
    private static final String AMAZON_VIDEO = "https://d2zumfut4741yc.cloudfront.net/0f74fab4-17a2-4bdf-ad51-c9a5cfcd29d2/AppleHLS1/KLYU_004_SHOOTING_SIDE.m3u8";

    public List<Video> loadVideos(String filename) {
        List<Video> videos = new ArrayList<>();
        InputStream is = getClass().getResourceAsStream(filename);
        try (BufferedReader br =  new BufferedReader(new InputStreamReader(is))) {
            int row = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if (row > 0) {
                    videos.add(createVideo(line.split(SEMICOLON)));
                }
                row++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return videos;
    }

    private Video createVideo(String[] values) {
        String player = values[6].contains(YOUTUBE)? YOUTUBE: PEECKO;
        Video video = new Video()
            .setCode(trim(values[0]))
            .setCategory(trim(values[1]))
            .setTitle(trim(values[2]))
            .setDuration(trim(values[3]))
            .setCoach(trim(values[4]))
            .setImage(trim(values[5]))
            .setUrl(trim(values[6]))
            .setAudience(trim(values[7]))
            .setIntensity(trim(values[8]))
            .setTags(asList(values[9]))
            .setDescription(trim(values[10]))
            .setResume(trim(values[11]))
            .setPlayer(player);
        if (PEECKO.equals(video.getPlayer())) {
            video.setUrl(AMAZON_VIDEO);
        }
        Coach coach = CoachRepository.find(video.getCoach());
        video.setCoachEmail(coach.getEmail());
        video.setCoachInstagram(coach.getInstagram());
        video.setCoachWebsite(coach.getWebsite());
        return video;
    }

    private String trim(String value) {
        return value != null? value.trim(): null;
    }

    private List<String> asList(String values) {
        if (values == null || values == "") {
            return new ArrayList<>();
        }
        String[] array = values.split(",");
        return Arrays.stream(array).map(String::trim).collect(Collectors.toList());
    }

}
