package com.peecko.api.web.rest;

import com.peecko.api.domain.*;
import com.peecko.api.repository.UserRepository;
import com.peecko.api.repository.VideoRepository;
import com.peecko.api.security.Licensed;
import com.peecko.api.utils.Common;
import com.peecko.api.utils.FileDownloadUtil;
import com.peecko.api.web.payload.request.CreatePlaylistRequest;
import com.peecko.api.web.payload.response.LibraryResponse;
import com.peecko.api.web.payload.response.MessageResponse;
import com.peecko.api.web.payload.response.TodayResponse;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.peecko.api.utils.Common.ERROR;

@RestController
@RequestMapping("/api/videos")
public class VideoController extends BaseController {

    final MessageSource messageSource;

    final VideoRepository videoRepository;

    final UserRepository userRepository;

    final ResourceLoader resourceLoader;

    public VideoController(MessageSource messageSource, VideoRepository videoRepository, UserRepository userRepository, ResourceLoader resourceLoader) {
        this.messageSource = messageSource;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
    }

    @Licensed
    @GetMapping("/today")
    public ResponseEntity<?> getTodayVideos() {
        String greeting = "Here is your weekly dose of Wellness support. Check back next week for more updates";
        String username = getUsername(userRepository);
        List<Video> videos = videoRepository.getTodayVideos(username);
        List<String> tags = Common.getVideoTags(videos);
        return ResponseEntity.ok(new TodayResponse(greeting, tags, videos));
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites() {
        String username = getUsername(userRepository);
        String greeting = "Here is your list of favorite videos, we are glad to know you keep up the good work!";
        List<Video> videos = videoRepository.getUserFavorites(username);
        List<String> tags = Common.getVideoTags(videos);
        return ResponseEntity.ok(new TodayResponse(greeting, tags, videos));
    }

    @GetMapping("/playlists")
    public ResponseEntity<?> getPlaylists() {
        String username = getUsername(userRepository);
        List<IdName> list = userRepository.getPlaylists(username);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/playlists/{listId}")
    public ResponseEntity<?> getPlaylist(@PathVariable Long listId) {
        String username = getUsername(userRepository);
        Optional<Playlist> optionalPlaylist = userRepository.getPlaylist(username, listId);
        return ResponseEntity.ofNullable(optionalPlaylist); // 404 Not Found
    }

    @Licensed
    @PostMapping("/playlists/")
    public ResponseEntity<?> createPlaylist(@Valid @RequestBody CreatePlaylistRequest request) {
        String name = request.getName();
        String username = getUsername(userRepository);
        if (userRepository.playlistExistsByName(username, name)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message("playlist.duplicate")));
        }
        Optional<Playlist> optionalPlaylist = userRepository.createPlaylist(username, name);
        return ResponseEntity.ofNullable(optionalPlaylist);
    }

    @PutMapping("/playlists/{listId}/{videoCode}")
    public ResponseEntity<?> addPlaylistVideoItem(@PathVariable Long listId, @PathVariable String videoCode) {
        String username = getUsername(userRepository);
        Video video = videoRepository.getVideo(username, videoCode);
        Playlist playlist = userRepository.addPlaylistVideoItem(username, listId, video);
        return ResponseEntity.ok(playlist);
    }

    @DeleteMapping("/playlists/{listId}/{videoCode}")
    public ResponseEntity<?> removePlaylistVideoItem(@PathVariable Long listId, @PathVariable String videoCode) {
        String username = getUsername(userRepository);
        Playlist playlist = userRepository.removePlaylistVideoItem(username, listId, videoCode);
        return ResponseEntity.ok(playlist);
    }

    @PutMapping("/playlists/listId/{videoCode}/{previousVideoCode}")
    public ResponseEntity<?> movePlaylistVideoItem(@PathVariable Long id, @PathVariable String videoCode, @PathVariable String previousVideoCode) {
        String username = getUsername(userRepository);
        Playlist playlist = userRepository.movePlaylistVideoItem(username, id, videoCode, previousVideoCode);
        return ResponseEntity.ok(playlist);
    }
    @GetMapping("/categories")
    public ResponseEntity<?> getLibrary() {
        String greeting = "All our video content under one roof, organized into wellness & fitness categories";
        String username = getUsername(userRepository);
        List<Category> categories = videoRepository.getLibrary(username);
        return ResponseEntity.ok(new LibraryResponse(greeting, categories));
    }

    @GetMapping("/categories/{code}")
    public ResponseEntity<?> getCategory(@PathVariable String code) {
        String username = getUsername(userRepository);
        Optional<Category> category = videoRepository.getCategory(code, username);
        return ResponseEntity.ofNullable(category); // 404 Not Found
    }

    @PutMapping("/favorites/{code}")
    public ResponseEntity<?> addFavorite(@PathVariable String code) {
        String username = getUsername(userRepository);
        videoRepository.addFavorite(username, code);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorites/{code}")
    public ResponseEntity<?> removeFavorite(@PathVariable String code) {
        String username = getUsername(userRepository);
        videoRepository.removeFavorite(username, code);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorites")
    public ResponseEntity<?> removeFavorites() {
        String username = getUsername(userRepository);
        videoRepository.removeFavorites(username);
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

    private String message(String code) {
        Locale locale = geActiveLocale(userRepository);
        return messageSource.getMessage(code, null, locale);
    }

}
