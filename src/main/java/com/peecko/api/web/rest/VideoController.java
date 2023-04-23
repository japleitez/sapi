package com.peecko.api.web.rest;

import com.peecko.api.domain.Category;
import com.peecko.api.domain.Video;
import com.peecko.api.repository.VideoRepository;
import com.peecko.api.web.payload.response.LibraryResponse;
import com.peecko.api.web.payload.response.TodayResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    final VideoRepository videoRepository;

    public VideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayVideos() {
        String greeting = "Here is your weekly dose of Wellness support. Check back next week for more updates";
        List<String> tags = List.of("all", "energy", "endurance", "relax", "learn");
        List<Video> videos = videoRepository.getTodayVideos();
        return ResponseEntity.ok(new TodayResponse(greeting, tags, videos));
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites() {
        String greeting = "Here is your weekly dose of Wellness support. Check back next week for more updates";
        List<String> tags = List.of("all", "energy", "endurance", "relax", "learn");
        List<Video> videos = videoRepository.getTodayVideos();
        return ResponseEntity.ok(new TodayResponse(greeting, tags, videos));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getLibrary() {
        String greeting = "All our video content under one roof, organized into wellness & fitness categories";
        List<Category> categories = videoRepository.getCategories();
        return ResponseEntity.ok(new LibraryResponse(greeting, categories));
    }

    @GetMapping("/categories/{code}")
    public ResponseEntity<?> getCategory(@PathVariable String code) {
        return ResponseEntity.ofNullable(videoRepository.getCategory(code)); // 404 Not Found
    }

}
