package com.peecko.api.web.rest;

import com.peecko.api.domain.Video;
import com.peecko.api.repository.TodayRepository;
import com.peecko.api.web.payload.response.TodayResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/today")
public class TodayController {

    final TodayRepository todayRepository;

    public TodayController(TodayRepository todayRepository) {
        this.todayRepository = todayRepository;
    }

    @GetMapping("/videos")
    public ResponseEntity<?> getVideos() {
        String greeting = "Here is your weekly dose of Wellness support. Check back next week for more updates";
        List<String> tags = List.of("all", "energy", "endurance", "relax", "learn");
        List<Video> videos = todayRepository.getVideos();
        return ResponseEntity.ok(new TodayResponse(greeting, tags, videos));
    }

}
