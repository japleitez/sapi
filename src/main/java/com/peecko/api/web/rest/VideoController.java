package com.peecko.api.web.rest;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.PlayList;
import com.peecko.api.domain.VideoItem;
import com.peecko.api.domain.dto.*;
import com.peecko.api.repository.PlayListRepo;
import com.peecko.api.repository.VideoItemRepo;
import com.peecko.api.repository.VideoRepo;
import com.peecko.api.security.Licensed;
import com.peecko.api.service.ApsUserService;
import com.peecko.api.service.LabelService;
import com.peecko.api.service.PlayListService;
import com.peecko.api.service.VideoService;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.request.CreatePlaylistRequest;
import com.peecko.api.web.payload.response.LibraryResponse;
import com.peecko.api.web.payload.response.MessageResponse;
import com.peecko.api.web.payload.response.TodayResponse;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.peecko.api.utils.Common.ERROR;

@RestController
@RequestMapping("/api/videos")
public class VideoController extends BaseController {

    final VideoRepo videoRepo;
    final PlayListRepo playListRepo;
    final LabelService labelService;
    final VideoService videoService;
    final VideoItemRepo videoItemRepo;
    final MessageSource messageSource;
    final ApsUserService apsUserService;
    final PlayListService playListService;

    public VideoController(VideoRepo videoRepo, PlayListRepo playListRepo, LabelService labelService, VideoService videoService, VideoItemRepo videoItemRepo, MessageSource messageSource, ApsUserService apsUserService, PlayListService playListService) {
        this.videoRepo = videoRepo;
        this.playListRepo = playListRepo;
        this.labelService = labelService;
        this.videoService = videoService;
        this.videoItemRepo = videoItemRepo;
        this.messageSource = messageSource;
        this.apsUserService = apsUserService;
        this.playListService = playListService;
    }

    @Licensed
    @GetMapping("/today")
    public ResponseEntity<?> getTodayVideos() {
        String greeting = labelService.getLabel("greeting.today");
        List<VideoDTO> videos = videoService.getTodayVideos(getApsUserId());
        List<String> tags = Common.getVideoTags(videos);
        return ResponseEntity.ok(new TodayResponse(greeting, videos, tags));
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites() {
        String greeting = labelService.getLabel("greeting.favorites");
        List<VideoDTO> videos = videoService.findUserFavoriteVideos(getApsUserId());
        List<String> tags = Common.getVideoTags(videos);
        return ResponseEntity.ok(new TodayResponse(greeting, videos, tags));
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getLibrary() {
        String greeting = labelService.getLabel("greeting.library");
        List<CategoryDTO> categories = videoService.getLibrary(getApsUserId());
        return ResponseEntity.ok(new LibraryResponse(greeting, categories));
    }

    @GetMapping("/playlists")
    public ResponseEntity<?> getPlaylists() {
        ApsUser apsUser = apsUserService.findByUsername(getUsername());
        List<IdName> listIdNames = playListService.getPlayListIdNames(apsUser);
        return ResponseEntity.ok(listIdNames);
    }

    @GetMapping("/playlists/{listId}")
    public ResponseEntity<?> getPlaylist(@PathVariable Long listId) {
        Locale locale = apsUserService.getUserLocale(getUsername());
        PlaylistDTO playlistDTO = playListService.getPlayListDTO(getApsUserId(), listId);
        return ResponseEntity.ok(Objects.requireNonNullElseGet(playlistDTO, () -> new MessageResponse(ERROR, message(locale, "playlist.invalid"))));
    }

    @DeleteMapping("/playlists/{listId}")
    public ResponseEntity<?> deletePlaylist(@PathVariable Long listId) {
        playListService.deletePlayList(listId);
        ApsUser apsUser = apsUserService.findByUsername(getUsername());
        List<IdName> listIdNames = playListService.getPlayListIdNames(apsUser);
        return ResponseEntity.ok(listIdNames);
    }

    @Licensed
    @PostMapping("/playlists/")
    public ResponseEntity<?> createPlaylist(@Valid @RequestBody CreatePlaylistRequest request) {
        ApsUser apsUser = apsUserService.findByUsername(getUsername());
        if (playListService.existsPlayList(apsUser, request.getName())) {
            Locale locale = apsUserService.getUserLocale(getUsername());
            return ResponseEntity.ok(new MessageResponse(ERROR, message(locale, "playlist.duplicate")));
        }
        PlaylistDTO playlistDTO = playListService.createPlayList(apsUser, request.getName());
        return ResponseEntity.ok(playlistDTO);
    }

    @PutMapping("/playlists/{playlistId}/{videoCode}")
    public ResponseEntity<?> addPlaylistVideoItem(@PathVariable Long playlistId, @PathVariable String videoCode) {
        Locale locale = apsUserService.getUserLocale(getUsername());
        PlayList playList = playListRepo.findById(playlistId).orElse(null);
        if (playList == null) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message(locale,"playlist.invalid")));
        }
        if (videoRepo.findByCode(videoCode).isEmpty()) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message(locale,"video.invalid")));
        }
        if (videoItemRepo.existsByCodeAndPlayList(videoCode, playList)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message(locale,"playlist.video.added.already")));
        }
        VideoItem newVideoItem = new VideoItem(videoCode);
        playListService.addVideoItemToTop(playlistId, newVideoItem);
        PlaylistDTO playlistDTO = playListService.getPlayListDTO(getApsUserId(), playlistId);
        return ResponseEntity.ok(playlistDTO);
    }

    @PutMapping("/playlists/{listId}/{videoCode}/drag-beneath/{targetVideoCode}")
    public  ResponseEntity<?> dragPlaylistVideoItem(@PathVariable Long listId, @PathVariable String videoCode, @PathVariable String targetVideoCode) {
        Locale locale = apsUserService.getUserLocale(getUsername());
        PlayList playList = playListRepo.findById(listId).orElse(null);
        if (playList == null) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message(locale,"playlist.invalid")));
        }
        if (!videoItemRepo.existsByCodeAndPlayList(videoCode, playList)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message(locale,"video.item.invalid")));
        }
        if (!videoItemRepo.existsByCodeAndPlayList(targetVideoCode, playList)) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message(locale,"video.item.new.previous.invalid")));
        }
        playListService.moveVideoItemBelowAnother(listId, videoCode, targetVideoCode);
        PlaylistDTO playlistDTO = playListService.getPlayListDTO(getApsUserId(), listId);
        return ResponseEntity.ok(playlistDTO);
    }

    @DeleteMapping("/playlists/{playlistId}/bulk-delete")
    public ResponseEntity<?> removePlaylistVideoItems(@PathVariable Long playlistId, @RequestBody List<String> codes) {
        Locale locale = apsUserService.getUserLocale(getUsername());
        PlayList playList = playListRepo.findById(playlistId).orElse(null);
        if (playList == null) {
            return ResponseEntity.ok(new MessageResponse(ERROR, message(locale,"playlist.invalid")));
        }
        playListService.removeVideoItems(playlistId, codes);
        PlaylistDTO playlistDTO = playListService.getPlayListDTO(getApsUserId(), playlistId);
        return ResponseEntity.ok(playlistDTO);
    }

    @GetMapping("/categories/{code}")
    public ResponseEntity<?> getCategory(@PathVariable String code) {
        CategoryDTO category = videoService.getCategory(getApsUserId(), code);
        return ResponseEntity.ofNullable(category); // 404 Not Found
    }

    @PutMapping("/favorites/{code}")
    public ResponseEntity<?> addFavorite(@PathVariable String code) {
        videoService.addFavoriteVideo(getApsUserId(), code);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorites/{code}")
    public ResponseEntity<?> removeFavorite(@PathVariable String code) {
        videoService.removeFavoriteVideo(getApsUserId(), code);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/favorites")
    public ResponseEntity<?> removeFavorites() {
        videoService.deleteFavoriteVideosForUser(getApsUserId());
        return ResponseEntity.ok().build();
    }

    private String message(Locale locale, String code) {
        return messageSource.getMessage(code, null, locale);
    }

    private Long getApsUserId() {
        return apsUserService.findIdByUsername(getUsername());
    }

}
