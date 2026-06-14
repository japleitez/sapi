package com.peecko.api.web.rest;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.*;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.security.Login;
import com.peecko.api.service.*;
import com.peecko.api.security.Licensed;
import com.peecko.api.web.payload.request.CreatePlaylistRequest;
import com.peecko.api.web.payload.response.LibraryResponse;
import com.peecko.api.web.payload.response.Message;
import com.peecko.api.web.payload.response.TodayResponse;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.peecko.api.utils.Common.ERROR;

@RestController
@RequestMapping("/api/videos")
public class VideoResource extends BaseResource {

    final LabelService labelService;
    final VideoService videoService;
    final MessageSource messageSource;
    final PlayListService playListService;
    final PlayListItemService playListItemService;
    final VideoCategoryService videoCategoryService;


    public VideoResource(LabelService labelService, VideoService videoService, MessageSource messageSource, PlayListService playListService, PlayListItemService playListItemService, VideoCategoryService videoCategoryService) {
        this.labelService = labelService;
        this.videoService = videoService;
        this.messageSource = messageSource;
        this.playListService = playListService;
        this.playListItemService = playListItemService;
        this.videoCategoryService = videoCategoryService;
    }

    /**
     * Get today videos.
     */
    @Licensed
    @GetMapping("/today")
    public ResponseEntity<TodayResponse> getTodayVideos() {
        log.info("getTodayVideos");
        ApsUser apsUser = Login.getUser();
        String greeting = labelService.getCachedLabel("greeting.today", apsUser.getLanguage());
        List<Video> todayVideos = videoService.getCachedTodayVideos(Lang.AA);
        videoService.resolveFavorites(todayVideos, apsUser.getId());
        List<VideoDTO> videos = videoService.toVideoDTOs(todayVideos, apsUser.getLanguage());
        List<String> tags = videoService.getVideoTags(videos, apsUser.getLanguage());
        return ResponseEntity.ok(new TodayResponse(greeting, videos, tags));
    }

    /**
     * Get latest N videos by categories and user's language.
     */
    @GetMapping("/categories")
    public ResponseEntity<LibraryResponse> getLibrary() {
        log.info("getLibrary");
        ApsUser apsUser = Login.getUser();
        String greeting = labelService.getCachedLabel("greeting.library", apsUser.getLanguage());
        Map<VideoCategory, List<Video>>  latestVideos = videoService.getCachedLatestVideo(Lang.AA);
        videoService.resolveFavorites(latestVideos, apsUser.getId());
        List<CategoryDTO> categories = videoService.toCategoryDTOs(latestVideos, apsUser.getLanguage());
        return ResponseEntity.ok(new LibraryResponse(greeting, categories));
    }

    /**
     * Get the videos by category and user's language.
     */
    @GetMapping("/categories/{code}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable String code) {
        log.info("getCategory: code={}", code);
        VideoCategory category = videoCategoryService.findByCode(code).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        ApsUser apsUser = Login.getUser();
        List<Video> videos = videoService.getCachedVideosByCategoryAndLang(category, Lang.AA);
        videoService.resolveFavorites(videos, apsUser.getId());
        CategoryDTO categoryDTO = videoService.toCategoryDTO(category, videos, apsUser.getLanguage());
        return ResponseEntity.ok(categoryDTO);
    }

    /**
     * Get the user's favorite videos.
     */
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites() {
        log.info("getFavorites");
        ApsUser apsUser = Login.getUser();
        String greeting = labelService.getCachedLabel("greeting.favorites", apsUser.getLanguage());
        List<Video> favoriteVideos = videoService.findUserFavoriteVideos(apsUser.getId());
        List<VideoDTO> videos = videoService.toVideoDTOs(favoriteVideos, apsUser.getLanguage());
        List<String> tags = videoService.getVideoTags(videos, apsUser.getLanguage());
        return ResponseEntity.ok(new TodayResponse(greeting, videos, tags));
    }

    /**
     * Get the user's playlists.
     */
    @GetMapping("/playlists")
    public ResponseEntity<?> getPlaylists() {
        log.info("getPlaylists");
        List<IdName> listIdNames = playListService.getPlayListsAsIdNames(Login.getUser());
        return ResponseEntity.ok(listIdNames);
    }

    /**
     * Get the playlist by its id.
     */
    @GetMapping("/playlists/{id}")
    public ResponseEntity<?> getPlaylist(@PathVariable Long id) {
        log.info("getPlaylist: id={}", id);
        PlayListDTO playlist = playListService.getPlayListAsDTO(id, Login.getUserId());
        return ResponseEntity.ok(Objects.requireNonNullElseGet(playlist, () -> new Message(ERROR, message("playlist.invalid"))));
    }

    /**
     * Create a new playlist with the given name.
     */
    @Licensed
    @PostMapping("/playlists/")
    public ResponseEntity<?> createPlaylist(@Valid @RequestBody CreatePlaylistRequest request) {
        log.info("createPlaylist: name={}", request.name());
        if (!StringUtils.hasText(request.name())) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.name.required")));
        }
        if (playListService.existsPlayListByName(Login.getUser(), request.name())) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.duplicate")));
        }
        Long userId = Login.getUserId();
        PlayList created = playListService.createPlayList(userId, request.name());
        PlayListDTO playlistDTO = playListService.toPlayListDTO(created);
        playlistDTO.setUsername(Login.getUser().getUsername());
        return ResponseEntity.ok(playlistDTO);
    }

    /**
     * Delete a playlist by its id.
     */
    @DeleteMapping("/playlists/{id}")
    public ResponseEntity<List<IdName>> deletePlaylist(@PathVariable Long id) {
        log.info("deletePlaylist: id={}", id);
        playListService.deletePlayList(id);
        List<IdName> listIdNames = playListService.getPlayListsAsIdNames(Login.getUser());
        return ResponseEntity.ok(listIdNames);
    }

    /**
     * Add a video to the specified playlist.
     */
    @PutMapping("/playlists/{playListId}/{videoCode}")
    public ResponseEntity<?> addVideoToPlayList(@PathVariable Long playListId, @PathVariable String videoCode) {
        log.info("addVideoToPlayList: playListId={}, videoCode={}", playListId, videoCode);
        if (!playListService.existsById(playListId)) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.invalid")));
        }
        if (!videoService.existsByCode(videoCode)) {
            return ResponseEntity.ok(new Message(ERROR, message("video.invalid")));
        }
        if (playListItemService.existsByPlayListIdAndCode(playListId, videoCode)) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.video.added.already")));
        }
        playListService.addVideoAtBottom(playListId, videoCode);
        PlayListDTO playlistDTO = playListService.getPlayListAsDTO(playListId, Login.getUserId());
        return ResponseEntity.ok(playlistDTO);
    }


    /**
     * Move a video item bellow another in the playlist.
     */
    @PutMapping("/playlists/{playListId}/{videoCode}/drag-beneath/{targetVideoCode}")
    public  ResponseEntity<?> moveVideoItemBelowAnother(@PathVariable Long playListId, @PathVariable String videoCode, @PathVariable String targetVideoCode) {
        log.info("moveVideoItemBelowAnother: playListId={}, videoCode={}, targetVideoCode={}", playListId, videoCode, targetVideoCode);
        if (!playListService.existsById(playListId)) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.invalid")));
        }
        if (!playListItemService.existsByPlayListIdAndCode(playListId, videoCode)) {
            return ResponseEntity.ok(new Message(ERROR, message("video.item.invalid")));
        }
        if (!"top".equals(targetVideoCode) && !playListItemService.existsByPlayListIdAndCode(playListId, targetVideoCode)) {
            return ResponseEntity.ok(new Message(ERROR, message("video.item.new.previous.invalid")));
        }
        if ("top".equals(targetVideoCode)) {
            playListService.moveVideoToTop(playListId, videoCode);
        } else  {
            playListService.dragVideoAfter(playListId, videoCode, targetVideoCode);
        }
        PlayListDTO playlistDTO = playListService.getPlayListAsDTO(playListId, Login.getUserId());
        return ResponseEntity.ok(playlistDTO);
    }

    /**
     * Delete video items from the playlist.
     */
    @DeleteMapping("/playlists/{playListId}/bulk-delete")
    public ResponseEntity<?> removePlaylistVideoItems(@PathVariable Long playListId, @RequestBody List<String> codes) {
        log.info("removePlaylistVideoItems: playListId={}, codes={}", playListId, codes);
        if (!playListService.existsById(playListId)) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.invalid")));
        }
        playListService.removeVideosFromPlaylist(playListId, codes);
        PlayListDTO playlistDTO = playListService.getPlayListAsDTO(playListId, Login.getUserId());
        return ResponseEntity.ok(playlistDTO);
    }

    /**
     * Add a video to the user's favorite video list.
     */
    @PutMapping("/favorites/{videoCode}")
    public ResponseEntity<?> addFavorite(@PathVariable String videoCode) {
        log.info("addFavorite: videoCode={}", videoCode);
        videoService.addUserFavoriteVideo(Login.getUserId(), videoCode);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove a video from the user's favorite video list.
     */
    @DeleteMapping("/favorites/{videoCode}")
    public ResponseEntity<?> removeFavorite(@PathVariable String videoCode) {
        log.info("removeFavorite: videoCode={}", videoCode);
        videoService.removeUserFavoriteVideo(Login.getUserId(), videoCode);
        return ResponseEntity.ok().build();
    }

    /**
     * Clear the user's favorite video list.
     */
    @DeleteMapping("/favorites")
    public ResponseEntity<?> removeFavorites() {
        log.info("removeFavorites");
        videoService.deleteFavoriteVideosForUser(Login.getUserId());
        return ResponseEntity.ok().build();
    }

    private String message(String code) {
        return messageSource.getMessage(code, null, Login.getUserLocale());
    }

}
