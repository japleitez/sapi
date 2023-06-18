package com.peecko.api.web.rest;

import com.peecko.api.domain.Category;
import com.peecko.api.domain.User;
import com.peecko.api.domain.Video;
import com.peecko.api.repository.UserRepository;
import com.peecko.api.repository.VideoRepository;
import com.peecko.api.security.Licensed;
import com.peecko.api.utils.Common;
import com.peecko.api.utils.FileDownloadUtil;
import com.peecko.api.web.payload.response.LibraryResponse;
import com.peecko.api.web.payload.response.TodayResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController extends BaseController {

    final VideoRepository videoRepository;

    final UserRepository userRepository;

    final ResourceLoader resourceLoader;

    public VideoController(VideoRepository videoRepository, UserRepository userRepository, ResourceLoader resourceLoader) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
    }

    @Licensed
    @GetMapping("/today")
    public ResponseEntity<?> getTodayVideos() {
        String greeting = "Here is your weekly dose of Wellness support. Check back next week for more updates";
        User user = getActiveUser(userRepository);
        List<Video> videos = videoRepository.getTodayVideos(user.username());
        List<String> tags = Common.getVideoTags(videos);
        return ResponseEntity.ok(new TodayResponse(greeting, tags, videos));
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites() {
        User user = getActiveUser(userRepository);
        String greeting = "Here is your weekly dose of Wellness support. Check back next week for more updates";
        List<Video> videos = videoRepository.getUserFavorites(user.username());
        List<String> tags = Common.getVideoTags(videos);
        return ResponseEntity.ok(new TodayResponse(greeting, tags, videos));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getLibrary() {
        String greeting = "All our video content under one roof, organized into wellness & fitness categories";
        User user = getActiveUser(userRepository);
        List<Category> categories = videoRepository.getLibrary(user.username());
        return ResponseEntity.ok(new LibraryResponse(greeting, categories));
    }

    @GetMapping("/categories/{code}")
    public ResponseEntity<?> getCategory(@PathVariable String code) {
        User user = getActiveUser(userRepository);
        return ResponseEntity.ofNullable(videoRepository.getCategory(code, user.username())); // 404 Not Found
    }

    @PutMapping("/favorites/{code}")
    public ResponseEntity<?> addFavorite(@PathVariable String code) {
        User user = getActiveUser(userRepository);
        videoRepository.addFavorite(user.username(), code);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorites/{code}")
    public ResponseEntity<?> removeFavorite(@PathVariable String code) {
        User user = getActiveUser(userRepository);
        videoRepository.removeFavorite(user.username(), code);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/fetch/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileCode") String fileCode) {
        FileDownloadUtil util = new FileDownloadUtil();
        Resource resource;
        try {
            resource = util.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
        if (resource == null) {
            return new ResponseEntity<>("File not found", HttpStatus.NOT_FOUND);
        }
        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .body(resource);
    }

}
