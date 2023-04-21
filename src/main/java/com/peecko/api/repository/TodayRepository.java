package com.peecko.api.repository;

import com.peecko.api.domain.Video;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TodayRepository {
    private static final String SEMICOLON = ";";
    private static final List<Video> videos = new ArrayList<>();

    public List<Video> getVideos() {
        if (videos.isEmpty()) {
            loadVideos();
        }
        return videos;
    }

    private void loadVideos()  {
        InputStream is = getClass().getResourceAsStream("/data/today.csv");
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
    }

    private Video createVideo(String[] values) {
        return new Video()
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
            .setResume(trim(values[11]));
    }

    private String trim(String value) {
        return value != null? value.trim(): null;
    }

    private List<String> asList(String values) {
        if (values == null) {
            return new ArrayList<>();
        }
        String[] array = values.split(",");
        return Arrays.stream(array).map(String::trim).collect(Collectors.toList());
    }

}
