package com.peecko.api.service;

import com.peecko.api.domain.TodayVideo;
import com.peecko.api.domain.Video;
import com.peecko.api.repository.TodayVideoRepo;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Select N random videos
 * int N = 3;
 * videoSelector.setVideos(videos);
 * List<Video> selectedVideos = videoSelector.selectNRandomVideos(N);
 */
@Service
public class VideoSelector {

    //TODO transfer this class to backoffice project
    //TODO this is heavy computation and should not be part of the API
    //TODO take the today_video.xml for the creation of the today video entity as well

    final TodayVideoRepo todayVideoRepo;

    @Setter
    private List<Video> videos = new ArrayList<>();
    private final Random random = new Random();

    public VideoSelector(TodayVideoRepo todayVideoRepo) {
        this.todayVideoRepo = todayVideoRepo;
    }

    private double calculateWeight(Video video) {
        long daysSincePublication = video.getDaysSincePublication();
        double recencyFactor = 1.0 / (daysSincePublication + 1); // Recent videos get higher weight
        double selectionFactor = 1.0 / (video.selectionCount + 1); // Videos selected less often get higher weight
        return recencyFactor * selectionFactor;
    }

    public Video selectRandomVideo() {
        double totalWeight = 0.0;
        for (Video video : videos) {
            totalWeight += calculateWeight(video);
        }

        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;

        for (Video video : videos) {
            cumulativeWeight += calculateWeight(video);
            if (cumulativeWeight >= randomValue) {
                video.incrementSelectionCount();
                //TODO save video to update publication date and selection count
                return video;
            }
        }

        return null; // Should never reach here
    }

    public List<Video> selectNRandomVideos(int N) {
        List<Video> selectedVideos = new ArrayList<>();
        while (selectedVideos.size() < N) {
            Video selectedVideo = selectRandomVideo();
            if (!selectedVideos.contains(selectedVideo)) {
                selectedVideos.add(selectedVideo);
            }
        }
        Set<Long> ids = selectedVideos.stream().map(Video::getId).collect(Collectors.toSet());
        TodayVideo todayVideo = new TodayVideo();
        todayVideo.setVideoIds(ids);
        todayVideo.setReleaseDate(LocalDate.now());
        todayVideoRepo.save(todayVideo);
        return selectedVideos;
    }

}
