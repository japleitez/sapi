package com.peecko.api.repository.fake;

import com.peecko.api.domain.dto.*;
import com.peecko.api.utils.Common;
import com.peecko.api.utils.VideoLoader;
import org.apache.commons.text.CaseUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class VideoRepository {

    private static final List<VideoDTO> TODAY_VIDEOS = new LinkedList<>();
    private static final List<CategoryDTO> CATEGORIES = new LinkedList<>();
    private static final List<CategoryDTO> LIBRARY = new LinkedList<>();
    public static final HashMap<String, List<String>> FAVORITES =  new LinkedHashMap<>();
    public static final Set<VideoDTO> ALL_VIDEOS = new HashSet<>();

    private static boolean loaded = false;

    public static final HashMap<String, List<PlaylistDTO>> PLAYLISTS = new HashMap<>();


    public List<VideoDTO> getTodayVideos(String user) {
        loadVideos();
        return decorateVideo(TODAY_VIDEOS, user);
    }

    public List<CategoryDTO> getLibrary(String user) {
        loadVideos();
        return LIBRARY.stream().map(categoryDTO -> decorateCategory(categoryDTO, user)).collect(Collectors.toList());
    }

    public Optional<CategoryDTO> getCategory(String code, String user) {
        loadVideos();
        return CATEGORIES.stream().filter(categoryDTO -> categoryDTO.getCode().equals(code)).map(categoryDTO -> decorateCategory(categoryDTO, user)).findFirst();
    }

    public List<VideoDTO> getUserFavorites(String username) {
        loadVideos();
        List<String> videoCodes = getUserFavoriteVideoCodes(username);
        return ALL_VIDEOS.stream()
            .filter(video -> videoCodes.contains(video.getCode()))
            .map(video -> {
                VideoDTO clone = Common.clone(video);
                clone.setFavorite(true);
                return clone;
            })
            .collect(Collectors.toList());
    }

    public void addFavorite(String username, String videoCode) {
        loadVideos();
        Optional<VideoDTO> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(videoCode)).findFirst();
        if (optional.isPresent()) {
            VideoDTO video = optional.get();
            List<String> videoCodes = getUserFavoriteVideoCodes(username);
            if (!videoCodes.contains(video.getCode())) {
                videoCodes.add(video.getCode());
                FAVORITES.put(username, videoCodes);
            }
        }
    }

    public void removeFavorite(String username, String videoCode) {
        loadVideos();
        Optional<VideoDTO> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(videoCode)).findFirst();
        if (optional.isPresent()) {
            VideoDTO video = optional.get();
            List<String> videoCodes = getUserFavoriteVideoCodes(username);
            if (videoCodes.contains(video.getCode())) {
                videoCodes.remove(video.getCode());
                FAVORITES.put(username, videoCodes);
            }
        }
    }

    public void removeFavorites(String user) {
        loadVideos();
        List<String> videos = FAVORITES.get(user);
        if (videos != null && !videos.isEmpty()) {
            videos.clear();
            FAVORITES.put(user, videos);
        }
    }

    private static void loadVideos() {
        if (loaded) {
            return;
        }
        loaded = true;
        List<String> videoCategories = List.of("YOGA", "PILATES", "CONDITIONING", "MEDITATION", "HEALTH RISK");
        ALL_VIDEOS.addAll(new VideoLoader().loadVideos("/data/videos.csv"));
        for(String categoryName: videoCategories) {
            CategoryDTO c1 = new CategoryDTO();
            c1.setCode(categoryName.substring(0,2).toLowerCase());
            c1.setTitle(CaseUtils.toCamelCase(categoryName, true, null));
            c1.setVideos(videosByCategory(categoryName));
            CATEGORIES.add(c1);
            TODAY_VIDEOS.add(c1.getVideos().get(0));
            CategoryDTO c2 = new CategoryDTO();
            c2.setCode(c1.getCode());
            c2.setTitle(c1.getTitle());
            c2.setVideos(copyVideos(c1.getVideos(), 3));
            LIBRARY.add(c2);
        }
        List<VideoDTO> plist =  ALL_VIDEOS.stream().filter(video -> video.getPlayer().equals("peecko")).collect(Collectors.toList());
        TODAY_VIDEOS.addAll(plist);
    }

    private static List<VideoDTO> videosByCategory(String category) {
        loadVideos();
        return new ArrayList<>(ALL_VIDEOS.stream().filter(v -> v.getCategory().equals(category)).collect(Collectors.toList()));
    }

    private static List<VideoDTO> copyVideos(List<VideoDTO> from, int num) {
        List<VideoDTO> list = new LinkedList<>();
        for(int i = 0; i < num; i++) {
            VideoDTO clone = Common.clone(from.get(i));
            list.add(clone);
        }
        return list;
    }

    public static List<String> getUserFavoriteVideoCodes(String username) {
        loadVideos();
        List<String> userFavorites = FAVORITES.get(username);
        if (userFavorites == null) {
            userFavorites = new LinkedList<>();
        }
        return userFavorites;
    }

    public VideoDTO getVideo(String username, String videoCode) {
        loadVideos();
        VideoDTO video = ALL_VIDEOS.stream().filter(v -> videoCode.equals(v.getCode())).findAny().orElse(null);
        if (video != null) {
            List<String> videoCodes = getUserFavoriteVideoCodes(username);
            VideoDTO clone = Common.clone(video);
            clone.setFavorite(videoCodes.contains(video.getCode()));
            return clone;
        }
        return null;
    }

    private List<VideoDTO> decorateVideo(List<VideoDTO> videos, String username) {
        List<String> favoriteVideoCodes = getUserFavoriteVideoCodes(username);
        return videos.stream().map(video -> {
            VideoDTO clone = Common.clone(video);
            clone.setFavorite(favoriteVideoCodes.contains(video.getCode()));
            return clone;
        }).collect(Collectors.toList());
    }

    private CategoryDTO decorateCategory(CategoryDTO categoryDTO, String user) {
        CategoryDTO nc = new CategoryDTO();
        nc.setCode(categoryDTO.getCode());
        nc.setTitle(categoryDTO.getTitle());
        nc.setVideos(decorateVideo(categoryDTO.getVideos(), user));
        return nc;
    }

    /**
     * Playlist
     * ------------------------------------------------------------------------------------*/

    private static Long PLAYLIST_NEXT_ID = 0L;
    public static String UP = "up";
    public static String DOWN = "down";
    public static String TOP = "top";
    public static String BOTTOM = "bottom";

    private static Long nextPlaylistId() {
        return ++PLAYLIST_NEXT_ID;
    }

    private void initUserPlaylist(String username) {
        if (PLAYLISTS.get(username) == null) {
            List<PlaylistDTO> playlistDTOS = new ArrayList<>();
            PLAYLISTS.put(username, playlistDTOS);
        }
    }

    public List<IdName> getPlaylistsIdNames(String username) {
        initUserPlaylist(username);
        return PLAYLISTS.get(username).stream().map(p -> createIdName(p)).toList();
    }

    private IdName createIdName(PlaylistDTO playlistDTO) {
        Integer counter = playlistDTO.getVideoItemDTOS() != null? playlistDTO.getVideoItemDTOS().size(): 0;
        return new IdName(playlistDTO.getId(), playlistDTO.getName(), counter);
    }

    public Optional<PlaylistDTO> getPlaylist(String username, Long id) {
        initUserPlaylist(username);
        Optional<PlaylistDTO> optionalPlaylist = PLAYLISTS.get(username).stream().filter(p -> id.equals(p.getId())).findAny();
        if (optionalPlaylist.isPresent()) {
            List<VideoItemDTO> videoItemDTOS = optionalPlaylist.get().getVideoItemDTOS();
            optionalPlaylist.get().setVideoItemDTOS(sortVideoItems(username, videoItemDTOS));
        }
        return optionalPlaylist;
    }

    public List<IdName> deletePlaylist(String username, Long id) {
        initUserPlaylist(username);
        List<PlaylistDTO> playlistDTOS = PLAYLISTS.get(username).stream().filter(p -> !id.equals(p.getId())).collect(Collectors.toList());
        PLAYLISTS.put(username, playlistDTOS);
        return getPlaylistsIdNames(username);
    }

    public Optional<PlaylistDTO> createPlaylist(String username, String listName) {
        initUserPlaylist(username);
        PlaylistDTO playlistDTO = new PlaylistDTO(username, nextPlaylistId(), listName, new ArrayList<>());
        PLAYLISTS.get(username).add(playlistDTO);
        return Optional.of(playlistDTO);
    }

    public PlaylistDTO addPlaylistVideoItem(String username, PlaylistDTO playlistDTO, VideoDTO video) {
        initUserPlaylist(username);
        if (playlistDTO == null || video == null) {
            return playlistDTO;
        }
        VideoItemDTO last = getLastVideoItem(playlistDTO);
        VideoItemDTO toAdd = new VideoItemDTO();
        toAdd.setCode(video.getCode());
        toAdd.setVideo(video);
        if (last != null) {
            last.setNext(video.getCode());
            toAdd.setPrevious(last.getCode());
        }
        playlistDTO.getVideoItemDTOS().add(toAdd);
        playlistDTO.setVideoItemDTOS(sortVideoItems(username, playlistDTO.getVideoItemDTOS()));
        return playlistDTO;
    }

    public boolean videoIsAlreadyAdded(String username, PlaylistDTO playlistDTO, VideoDTO video) {
        initUserPlaylist(username);
        String videoCode = video.getCode();
        return playlistDTO.getVideoItemDTOS().stream().filter(v -> videoCode.equals(v.getCode())).findAny().isPresent();
    }

    public PlaylistDTO removePlaylistVideoItem(String username, PlaylistDTO playlistDTO, VideoDTO video) {
        if (playlistDTO == null || video == null) {
            return playlistDTO;
        }
        String videoCode = video.getCode();
        Optional<VideoItemDTO> optionalVideoItem = playlistDTO.getVideoItemDTOS().stream().filter(v -> videoCode.equals(v.getCode())).findAny();
        if (optionalVideoItem.isPresent()) {
            List<VideoItemDTO> cleaned = new ArrayList<>();
            VideoItemDTO toRemove = optionalVideoItem.get();
            if (playlistDTO.getVideoItemDTOS().size() > 1) {
                VideoItemDTO previous = null;
                VideoItemDTO next = null;
                if (StringUtils.hasText(toRemove.getPrevious())) {
                    previous = playlistDTO.getVideoItemDTOS().stream().filter(v -> toRemove.getPrevious().equals(v.getCode())).findAny().orElse(null);
                }
                if (StringUtils.hasText(toRemove.getNext())) {
                    next = playlistDTO.getVideoItemDTOS().stream().filter(v -> toRemove.getNext().equals(v.getCode())).findAny().orElse(null);
                }
                if (previous != null && next != null) {
                    previous.setNext(next.getCode());
                    next.setPrevious(previous.getCode());
                } else if (previous == null && next != null) {
                    next.setPrevious(null);
                } else if (previous != null && next == null) {
                    previous.setNext(null);
                }
                cleaned = playlistDTO.getVideoItemDTOS().stream().filter(v -> !videoCode.equals(v.getCode())).toList();
            }
            playlistDTO.setVideoItemDTOS(sortVideoItems(username, cleaned));
        }
        return playlistDTO;
    }

    public PlaylistDTO movePlaylistVideoItem(String username, PlaylistDTO playlistDTO, VideoItemDTO currentNode, String direction) {
        if (playlistDTO.getVideoItemDTOS().size() < 2 || currentNode == null  ) {
            return playlistDTO;
        }
        if (UP.equalsIgnoreCase(direction)) {
            if (StringUtils.hasText(currentNode.getPrevious())) {
                VideoItemDTO currentPrevious = getVideoItem(playlistDTO, currentNode.getPrevious());
                if (StringUtils.hasText(currentPrevious.getPrevious())) {
                    String newPreviousVideoCode = currentPrevious.getPrevious();
                    playlistDTO = dragUpPlaylistVideoItem(username, playlistDTO, currentNode, newPreviousVideoCode);
                } else {
                    playlistDTO = dragUpPlaylistVideoItem(username, playlistDTO, currentNode, TOP);
                }
            }
        } else if (DOWN.equalsIgnoreCase(direction)) {
            if (StringUtils.hasText(currentNode.getNext())) {
                String newPreviousVideoCode = currentNode.getNext();
                playlistDTO = dragDownPlaylistVideoItem(username, playlistDTO, currentNode, newPreviousVideoCode);
            }
        }
        return playlistDTO;
    }

    public PlaylistDTO dragPlaylistVideoItem(String username, PlaylistDTO playlistDTO, VideoItemDTO videoItemDTO, String newPreviousVideoCode) {
        if (playlistDTO == null || videoItemDTO == null || !StringUtils.hasText(newPreviousVideoCode)) {
            return playlistDTO;
        }
        final String direction;
        if (TOP.equals(newPreviousVideoCode)) {
            direction = UP;
            if (!StringUtils.hasText(videoItemDTO.getPrevious())) {
                // current video is already at the top
                return playlistDTO;
            }
        } else {
            if (newPreviousVideoCode.equals(videoItemDTO.getPrevious())) {
                // new previous video is the current's previous video
                return playlistDTO;
            }
            VideoItemDTO newPreviousVideo = getVideoItem(playlistDTO, newPreviousVideoCode);
            direction = newPreviousVideo.getIndex() < videoItemDTO.getIndex()? UP: DOWN;
        }
        if (UP.equalsIgnoreCase(direction)) {
            if (TOP.equals(newPreviousVideoCode)) {
                playlistDTO = dragUpPlaylistVideoItem(username, playlistDTO, videoItemDTO, TOP);
            } else {
                playlistDTO = dragUpPlaylistVideoItem(username, playlistDTO, videoItemDTO, newPreviousVideoCode);
            }
        } else if (DOWN.equalsIgnoreCase(direction)) {
            playlistDTO = dragDownPlaylistVideoItem(username, playlistDTO, videoItemDTO, newPreviousVideoCode);
        }
        return playlistDTO;
    }

    public PlaylistDTO dragUpPlaylistVideoItem(String username, PlaylistDTO playlistDTO, VideoItemDTO current, String newPreviousVideoCode) {
        boolean moved = false;
        VideoItemDTO previous = getVideoItem(playlistDTO, current.getPrevious());
        VideoItemDTO next = getVideoItem(playlistDTO, current.getNext());
        VideoItemDTO newPrevious = null;
        if (TOP.equals(newPreviousVideoCode)) {
            newPrevious = getFirstVideoItem(playlistDTO);
        } else {
            newPrevious = getVideoItem(playlistDTO, newPreviousVideoCode);
        }
        try {
            previous.setNext(current.getNext());
            if (next != null) {
                next.setPrevious(previous.getCode());
            }
            if (TOP.equals(newPreviousVideoCode)) {
                newPrevious.setPrevious(current.getCode());
                current.setPrevious(null);
                current.setNext(newPrevious.getCode());
            } else {
                current.setPrevious(newPrevious.getCode());
                current.setNext(newPrevious.getNext());
                VideoItemDTO newNext = getVideoItem(playlistDTO, newPrevious.getNext());
                newNext.setPrevious(current.getCode());
                newPrevious.setNext(current.getCode());
            }
            moved = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (moved) {
            playlistDTO.setVideoItemDTOS(sortVideoItems(username, playlistDTO.getVideoItemDTOS()));
        }
        return playlistDTO;
    }

    public PlaylistDTO dragDownPlaylistVideoItem(String username, PlaylistDTO playlistDTO, VideoItemDTO current, String newPreviousVideoCode) {
        boolean moved = false;
        VideoItemDTO previous = getVideoItem(playlistDTO, current.getPrevious());
        VideoItemDTO next = getVideoItem(playlistDTO, current.getNext());
        VideoItemDTO newPrevious = null;
        if (BOTTOM.equals(newPreviousVideoCode)) {
            newPrevious = getLastVideoItem(playlistDTO);
        } else {
            newPrevious = getVideoItem(playlistDTO, newPreviousVideoCode);
        }
        try {
            if (previous == null) {
                next.setPrevious(null);
            } else {
                next.setPrevious(previous.getCode());
                previous.setNext(next.getCode());
            }
            VideoItemDTO nextNext = null;
            if (newPrevious.getCode().equals(next.getCode())) {
                if (StringUtils.hasText(next.getNext())) {
                    nextNext = getVideoItem(playlistDTO, next.getNext());
                    nextNext.setPrevious(current.getCode());
                    current.setNext(nextNext.getCode());
                } else {
                    current.setNext(null);
                }
                next.setNext(current.getCode());
                current.setPrevious(next.getCode());
            } else {
                // move current more down
                if (StringUtils.hasText(newPrevious.getNext())) {
                    nextNext = getVideoItem(playlistDTO, newPrevious.getNext());
                    nextNext.setPrevious(current.getCode());
                    current.setNext(nextNext.getCode());
                } else {
                    current.setNext(null);
                }
                newPrevious.setNext(current.getCode());
                current.setPrevious(newPrevious.getCode());
            }
            moved = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (moved) {
            playlistDTO.setVideoItemDTOS(sortVideoItems(username, playlistDTO.getVideoItemDTOS()));
        }
        return playlistDTO;
    }


    public VideoItemDTO getVideoItem(PlaylistDTO playlistDTO, String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        List<VideoItemDTO> list = sortVideoItems(playlistDTO.getUsername(), playlistDTO.getVideoItemDTOS());
        return playlistDTO.getVideoItemDTOS().stream().filter(v -> code.equals(v.getCode())).findAny().orElse(null);
    }

    private VideoItemDTO getFirstVideoItem(PlaylistDTO playlistDTO) {
        return playlistDTO.getVideoItemDTOS().stream().filter(v -> !StringUtils.hasText(v.getPrevious())).findAny().orElse(null);
    }
    private VideoItemDTO getLastVideoItem(PlaylistDTO playlistDTO) {
        return playlistDTO.getVideoItemDTOS().stream().filter(v -> !StringUtils.hasText(v.getNext())).findAny().orElse(null);
    }

    public boolean playlistExistsByName(String username, String playlistName) {
        if (!StringUtils.hasText(playlistName)) {
            return false;
        }
        if (PLAYLISTS.containsKey(username)) {
            return PLAYLISTS.get(username).stream().anyMatch(playlistDTO -> playlistName.equals(playlistDTO.getName()));
        }
        return false;
    }

    private List<VideoItemDTO> sortVideoItems(String username, List<VideoItemDTO> videoItemDTOS) {
        List<VideoItemDTO> sortedList =  new ArrayList<>();
        if (videoItemDTOS == null || videoItemDTOS.isEmpty()) {
            return sortedList;
        }
        Integer index = 0;
        List<String> favoriteVideoCodes = getUserFavoriteVideoCodes(username);
        VideoItemDTO videoItemDTO = videoItemDTOS.stream().filter(v -> !StringUtils.hasText(v.getPrevious())).findAny().get();
        videoItemDTO.setIndex(index);
        sortedList.add(videoItemDTO);
        while (StringUtils.hasText(videoItemDTO.getNext())) {
            String next = videoItemDTO.getNext();;
            videoItemDTO = videoItemDTOS.stream().filter(v -> next.equals(v.getCode())).findAny().get();
            videoItemDTO.getVideo().setFavorite(favoriteVideoCodes.contains(videoItemDTO.getVideo().getCode()));
            index++;
            videoItemDTO.setIndex(index);
            sortedList.add(videoItemDTO);
        }
        return sortedList;
    }

}
