package com.peecko.api.repository;

import com.peecko.api.domain.Video;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            .setCode(values[0])
            .setTitle(values[1])
            .setCategory(values[2])
            .setCoach(values[3])
            .setDuration(values[4])
            .setTags(Arrays.asList(values[5]))
            .setUrl(values[6]);
    }
}
