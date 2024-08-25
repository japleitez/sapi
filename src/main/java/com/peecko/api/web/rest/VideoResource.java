package com.peecko.api.web.rest;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.*;
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
    final VideoItemService videoItemService;
    final VideoCategoryService videoCategoryService;

    public VideoResource(LabelService labelService, VideoService videoService, MessageSource messageSource, PlayListService playListService, VideoItemService videoItemService, VideoCategoryService videoCategoryService) {
        this.labelService = labelService;
        this.videoService = videoService;
        this.messageSource = messageSource;
        this.playListService = playListService;
        this.videoItemService = videoItemService;
        this.videoCategoryService = videoCategoryService;
    }

    /**
     * Get today videos.
     */
    @Licensed
    @GetMapping("/today")
    public ResponseEntity<TodayResponse> getTodayVideos() {
        ApsUser apsUser = Login.getUser();
        String greeting = labelService.getCachedLabel("greeting.today", apsUser.getLanguage());
        List<Video> todayVideos = videoService.getCachedTodayVideos(apsUser.getLanguage());
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
        ApsUser apsUser = Login.getUser();
        String greeting = labelService.getCachedLabel("greeting.library", apsUser.getLanguage());
        Map<VideoCategory, List<Video>>  latestVideos = videoService.getCachedLatestVideo(apsUser.getLanguage());
        videoService.resolveFavorites(latestVideos, apsUser.getId());
        List<CategoryDTO> categories = videoService.toCategoryDTOs(latestVideos, apsUser.getLanguage());
        return ResponseEntity.ok(new LibraryResponse(greeting, categories));
    }

    /**
     * Get the videos by category and user's language.
     */
    @GetMapping("/categories/{code}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable String code) {
        VideoCategory category = videoCategoryService.findByCode(code).orElse(null);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        ApsUser apsUser = Login.getUser();
        List<Video> videos = videoService.getCachedVideosByCategoryAndLang(category, apsUser.getLanguage());
        videoService.resolveFavorites(videos, apsUser.getId());
        CategoryDTO categoryDTO = videoService.toCategoryDTO(category, videos, apsUser.getLanguage());
        return ResponseEntity.ok(categoryDTO);
    }

    /**
     * Get the user's favorite videos.
     */
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites() {
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
        List<IdName> listIdNames = playListService.getPlayListIdNames(Login.getUser());
        return ResponseEntity.ok(listIdNames);
    }

    /**
     * Get the playlist by its id.
     */
    @GetMapping("/playlists/{listId}")
    public ResponseEntity<?> getPlaylist(@PathVariable Long listId) {
        PlaylistDTO playlist = playListService.getPlayListAsDTO(listId, Login.getUserId());
        return ResponseEntity.ok(Objects.requireNonNullElseGet(playlist, () -> new Message(ERROR, message("playlist.invalid"))));
    }

    /**
     * Create a new playlist with the given name.
     */
    @Licensed
    @PostMapping("/playlists/")
    public ResponseEntity<?> createPlaylist(@Valid @RequestBody CreatePlaylistRequest request) {
        if (!StringUtils.hasText(request.getName())) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.name.required")));
        }
        if (playListService.existsPlayList(Login.getUser(), request.getName())) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.duplicate")));
        }
        PlayList created = playListService.createPlayList(Login.getUserId(), request.getName());
        PlaylistDTO playlistDTO = playListService.toPlayListDTO(created);
        return ResponseEntity.ok(playlistDTO);
    }

    /**
     * Delete a playlist by its id.
     */
    @DeleteMapping("/playlists/{listId}")
    public ResponseEntity<?> deletePlaylist(@PathVariable Long listId) {
        playListService.deletePlayList(listId);
        List<IdName> listIdNames = playListService.getPlayListIdNames(Login.getUser());
        return ResponseEntity.ok(listIdNames);
    }

    /**
     * Add a video to the specified playlist.
     */
    @PutMapping("/playlists/{playlistId}/{videoCode}")
    public ResponseEntity<?> addVideoToPlayList(@PathVariable Long playlistId, @PathVariable String videoCode) {
        if (playListService.existsById(playlistId)) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.invalid")));
        }
        if (videoService.existsByCode(videoCode)) {
            return ResponseEntity.ok(new Message(ERROR, message("video.invalid")));
        }
        if (videoItemService.existsByPlayListIdAndVideoCode(playlistId, videoCode)) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.video.added.already")));
        }
        VideoItem newVideoItem = new VideoItem(videoCode);
        playListService.addVideoItemToTop(playlistId, newVideoItem);
        PlaylistDTO playlistDTO = playListService.getPlayListAsDTO(playlistId, Login.getUserId());
        return ResponseEntity.ok(playlistDTO);
    }


    /**
     * Move a video item bellow another in the playlist.
     */
    @PutMapping("/playlists/{listId}/{videoCode}/drag-beneath/{targetVideoCode}")
    public  ResponseEntity<?> moveVideoItemBelowAnother(@PathVariable Long playlistId, @PathVariable String videoCode, @PathVariable String targetVideoCode) {
        if (playListService.existsById(playlistId)) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.invalid")));
        }
        if (!videoItemService.existsByPlayListIdAndVideoCode(playlistId, videoCode)) {
            return ResponseEntity.ok(new Message(ERROR, message("video.item.invalid")));
        }
        if (!videoItemService.existsByPlayListIdAndVideoCode(playlistId, targetVideoCode)) {
            return ResponseEntity.ok(new Message(ERROR, message("video.item.new.previous.invalid")));
        }
        playListService.moveVideoItemBelowAnother(playlistId, videoCode, targetVideoCode);
        PlaylistDTO playlistDTO = playListService.getPlayListAsDTO(playlistId, Login.getUserId());
        return ResponseEntity.ok(playlistDTO);
    }

    /**
     * Delete video items from the playlist.
     */
    @DeleteMapping("/playlists/{playlistId}/bulk-delete")
    public ResponseEntity<?> removePlaylistVideoItems(@PathVariable Long playlistId, @RequestBody List<String> codes) {
        if (playListService.existsById(playlistId)) {
            return ResponseEntity.ok(new Message(ERROR, message("playlist.invalid")));
        }
        playListService.removeVideoItems(playlistId, codes);
        PlaylistDTO playlistDTO = playListService.getPlayListAsDTO(playlistId, Login.getUserId());
        return ResponseEntity.ok(playlistDTO);
    }

    /**
     * Add a video to the user's favorite video list.
     */
    @PutMapping("/favorites/{code}")
    public ResponseEntity<?> addFavorite(@PathVariable String code) {
        videoService.addUserFavoriteVideo(Login.getUserId(), code);
        return ResponseEntity.ok().build();
    }

    /**
     * Remove a video from the user's favorite video list.
     */
    @DeleteMapping("/favorites/{code}")
    public ResponseEntity<?> removeFavorite(@PathVariable String code) {
        videoService.removeUserFavoriteVideo(Login.getUserId(), code);
        return ResponseEntity.ok().build();
    }

    /**
     * Clear the user's favorite video list.
     */
    @DeleteMapping("/favorites")
    public ResponseEntity<?> removeFavorites() {
        videoService.deleteFavoriteVideosForUser(Login.getUserId());
        return ResponseEntity.ok().build();
    }

    private String message(String code) {
        return messageSource.getMessage(code, null, Login.getLocale());
    }

}
