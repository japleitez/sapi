package com.peecko.api.web.rest;

import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.PlayList;
import com.peecko.api.domain.VideoCategory;
import com.peecko.api.domain.VideoItem;
import com.peecko.api.domain.dto.*;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.mapper.PlayListMapper;
import com.peecko.api.repository.PlayListRepo;
import com.peecko.api.repository.VideoCategoryRepo;
import com.peecko.api.repository.VideoItemRepo;
import com.peecko.api.repository.VideoRepo;
import com.peecko.api.repository.fake.UserRepository;
import com.peecko.api.repository.fake.VideoRepository;
import com.peecko.api.security.Licensed;
import com.peecko.api.service.ApsUserService;
import com.peecko.api.service.LabelService;
import com.peecko.api.service.PlayListService;
import com.peecko.api.service.VideoService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.peecko.api.utils.Common.ERROR;

@RestController
@RequestMapping("/api/videos")
public class VideoController extends BaseController {

    final MessageSource messageSource;
    final VideoRepository videoRepository;
    final UserRepository userRepository;
    final ResourceLoader resourceLoader;
    final VideoService videoService;
    final LabelService labelService;
    final ApsUserService apsUserService;

    final PlayListRepo playListRepo;
    final VideoRepo videoRepo;
    final VideoItemRepo videoItemRepo;
    final VideoCategoryRepo videoCategoryRepo;

    final PlayListService playListService;

    public VideoController(MessageSource messageSource, VideoRepository videoRepository, UserRepository userRepository, ResourceLoader resourceLoader, VideoService videoService, LabelService labelService, ApsUserService apsUserService, PlayListRepo playListRepo, VideoRepo videoRepo, VideoItemRepo videoItemRepo, VideoCategoryRepo videoCategoryRepo, PlayListService playListService) {
        this.messageSource = messageSource;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.resourceLoader = resourceLoader;
        this.videoService = videoService;
        this.labelService = labelService;
        this.apsUserService = apsUserService;
        this.playListRepo = playListRepo;
        this.videoRepo = videoRepo;
        this.videoItemRepo = videoItemRepo;
        this.videoCategoryRepo = videoCategoryRepo;
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
        List<IdName> playlistIdNames = playListService.getPlayListIdNames(apsUser);
        return ResponseEntity.ok(playlistIdNames);
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
        List<IdName> playlistIdNames = playListService.getPlayListIdNames(apsUser);
        return ResponseEntity.ok(playlistIdNames);
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
        CategoryDTO category = videoService.getAllVideosForCategory(getApsUserId(), code);
        return ResponseEntity.ofNullable(category); // 404 Not Found
    }

    /**
     * TO IMPLEMENT
     */
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
        Long apsUserId = getApsUserId();
        videoService.deleteAllFavoritesForUser(apsUserId);
        return ResponseEntity.ok().build();
    }

    private String message(Locale locale, String code) {
        return messageSource.getMessage(code, null, locale);
    }

    private Lang getApsUserLang() {
        return apsUserService.findLangByUsername(getUsername());
    }

    private Long getApsUserId() {
        return apsUserService.findIdByUsername(getUsername());
    }

    private String getUsername() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }

}
