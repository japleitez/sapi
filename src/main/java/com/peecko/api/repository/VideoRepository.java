package com.peecko.api.repository;

import com.peecko.api.domain.*;
import com.peecko.api.utils.Common;
import com.peecko.api.utils.VideoLoader;
import org.apache.commons.text.CaseUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class VideoRepository {

    private static final List<Video> TODAY_VIDEOS = new LinkedList<>();
    private static final List<Category> CATEGORIES = new LinkedList<>();
    private static final List<Category> LIBRARY = new LinkedList<>();
    public static final HashMap<String, List<String>> FAVORITES =  new LinkedHashMap<>();
    public static final Set<Video> ALL_VIDEOS = new HashSet<>();

    private static boolean loaded = false;

    public static final HashMap<String, List<Playlist>> PLAYLISTS = new HashMap<>();


    public List<Video> getTodayVideos(String user) {
        loadVideos();
        return decorateVideo(TODAY_VIDEOS, user);
    }

    public List<Category> getLibrary(String user) {
        loadVideos();
        return LIBRARY.stream().map(category -> decorateCategory(category, user)).collect(Collectors.toList());
    }

    public Optional<Category> getCategory(String code, String user) {
        loadVideos();
        return CATEGORIES.stream().filter(category -> category.getCode().equals(code)).map(category -> decorateCategory(category, user)).findFirst();
    }

    public List<Video> getUserFavorites(String username) {
        loadVideos();
        List<String> videoCodes = getUserFavoriteVideoCodes(username);
        return ALL_VIDEOS.stream()
            .filter(video -> videoCodes.contains(video.getCode()))
            .map(video -> {
                Video clone = Common.clone(video);
                clone.setFavorite(true);
                return clone;
            })
            .collect(Collectors.toList());
    }

    public void addFavorite(String username, String videoCode) {
        loadVideos();
        Optional<Video> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(videoCode)).findFirst();
        if (optional.isPresent()) {
            Video video = optional.get();
            List<String> videoCodes = getUserFavoriteVideoCodes(username);
            if (!videoCodes.contains(video.getCode())) {
                videoCodes.add(video.getCode());
                FAVORITES.put(username, videoCodes);
            }
        }
    }

    public void removeFavorite(String username, String videoCode) {
        loadVideos();
        Optional<Video> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(videoCode)).findFirst();
        if (optional.isPresent()) {
            Video video = optional.get();
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
            Category c1 = new Category();
            c1.setCode(categoryName.substring(0,2).toLowerCase());
            c1.setTitle(CaseUtils.toCamelCase(categoryName, true, null));
            c1.setVideos(videosByCategory(categoryName));
            CATEGORIES.add(c1);
            TODAY_VIDEOS.add(c1.getVideos().get(0));
            Category c2 = new Category();
            c2.setCode(c1.getCode());
            c2.setTitle(c1.getTitle());
            c2.setVideos(copyVideos(c1.getVideos(), 3));
            LIBRARY.add(c2);
        }
        List<Video> plist =  ALL_VIDEOS.stream().filter(video -> video.getPlayer().equals("peecko")).collect(Collectors.toList());
        TODAY_VIDEOS.addAll(plist);
    }

    private static List<Video> videosByCategory(String category) {
        loadVideos();
        return new ArrayList<>(ALL_VIDEOS.stream().filter(v -> v.getCategory().equals(category)).collect(Collectors.toList()));
    }

    private static List<Video> copyVideos(List<Video> from, int num) {
        List<Video> list = new LinkedList<>();
        for(int i = 0; i < num; i++) {
            Video clone = Common.clone(from.get(i));
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

    public Video getVideo(String username, String videoCode) {
        loadVideos();
        Video video = ALL_VIDEOS.stream().filter(v -> videoCode.equals(v.getCode())).findAny().orElse(null);
        if (video != null) {
            List<String> videoCodes = getUserFavoriteVideoCodes(username);
            Video clone = Common.clone(video);
            clone.setFavorite(videoCodes.contains(video.getCode()));
            return clone;
        }
        return null;
    }

    private List<Video> decorateVideo(List<Video> videos, String username) {
        List<String> favoriteVideoCodes = getUserFavoriteVideoCodes(username);
        return videos.stream().map(video -> {
            Video clone = Common.clone(video);
            clone.setFavorite(favoriteVideoCodes.contains(video.getCode()));
            return clone;
        }).collect(Collectors.toList());
    }

    private Category decorateCategory(Category category, String user) {
        Category nc = new Category();
        nc.setCode(category.getCode());
        nc.setTitle(category.getTitle());
        nc.setVideos(decorateVideo(category.getVideos(), user));
        return nc;
    }

    /**
     * Playlist
     * ------------------------------------------------------------------------------------*/

    private static Long PLAYLIST_NEXT_ID = 0L;
    private static String UP = "up";
    private static String DOWN = "down";
    private static String TOP = "top";
    private static String BOTTOM = "bottom";

    private static Long nextPlaylistId() {
        return ++PLAYLIST_NEXT_ID;
    }

    private void initUserPlaylist(String username) {
        if (PLAYLISTS.get(username) == null) {
            List<Playlist> playlists = new ArrayList<>();
            PLAYLISTS.put(username, playlists);
        }
    }

    public List<IdName> getPlaylistsIdNames(String username) {
        initUserPlaylist(username);
        return PLAYLISTS.get(username).stream().map(p -> createIdName(p)).toList();
    }

    private IdName createIdName(Playlist playlist) {
        Integer counter = playlist.getVideoItems() != null? playlist.getVideoItems().size(): 0;
        return new IdName(playlist.getId(), playlist.getName(), counter);
    }

    public Optional<Playlist> getPlaylist(String username, Long id) {
        initUserPlaylist(username);
        Optional<Playlist> optionalPlaylist = PLAYLISTS.get(username).stream().filter(p -> id.equals(p.getId())).findAny();
        if (optionalPlaylist.isPresent()) {
            List<VideoItem> videoItems = optionalPlaylist.get().getVideoItems();
            optionalPlaylist.get().setVideoItems(sortVideoItems(username, videoItems));
        }
        return optionalPlaylist;
    }

    public List<IdName> deletePlaylist(String username, Long id) {
        initUserPlaylist(username);
        List<Playlist> playlists = PLAYLISTS.get(username).stream().filter(p -> !id.equals(p.getId())).collect(Collectors.toList());
        PLAYLISTS.put(username, playlists);
        return getPlaylistsIdNames(username);
    }

    public Optional<Playlist> createPlaylist(String username, String listName) {
        initUserPlaylist(username);
        Playlist playlist = new Playlist(username, nextPlaylistId(), listName, new ArrayList<>());
        PLAYLISTS.get(username).add(playlist);
        return Optional.of(playlist);
    }

    public Playlist addPlaylistVideoItem(String username, Playlist playlist, Video video) {
        initUserPlaylist(username);
        if (playlist == null || video == null) {
            return playlist;
        }
        VideoItem last = getLastVideoItem(playlist);
        VideoItem toAdd = new VideoItem();
        toAdd.setCode(video.getCode());
        toAdd.setVideo(video);
        if (last != null) {
            last.setNext(video.getCode());
            toAdd.setPrevious(last.getCode());
        }
        playlist.getVideoItems().add(toAdd);
        playlist.setVideoItems(sortVideoItems(username, playlist.getVideoItems()));
        return playlist;
    }

    public boolean videoIsAlreadyAdded(String username, Playlist playlist, Video video) {
        initUserPlaylist(username);
        String videoCode = video.getCode();
        return playlist.getVideoItems().stream().filter(v -> videoCode.equals(v.getCode())).findAny().isPresent();
    }

    public Optional<Playlist> removePlaylistVideoItem(String username, Long listId, String videoCode) {
        Playlist playlist = getPlaylist(username, listId).orElse(null);
        if (playlist == null) {
            return Optional.empty();
        }
        Optional<VideoItem> optionalVideoItem = playlist.getVideoItems().stream().filter(v -> videoCode.equals(v.getCode())).findAny();
        if (optionalVideoItem.isPresent()) {
            List<VideoItem> cleaned = new ArrayList<>();
            VideoItem toRemove = optionalVideoItem.get();
            if (playlist.getVideoItems().size() > 1) {
                VideoItem previous = null;
                VideoItem next = null;
                if (StringUtils.hasText(toRemove.getPrevious())) {
                    previous = playlist.getVideoItems().stream().filter(v -> toRemove.getPrevious().equals(v.getCode())).findAny().orElse(null);
                }
                if (StringUtils.hasText(toRemove.getNext())) {
                    next = playlist.getVideoItems().stream().filter(v -> toRemove.getNext().equals(v.getCode())).findAny().orElse(null);
                }
                if (previous != null && next != null) {
                    previous.setNext(next.getCode());
                    next.setPrevious(previous.getCode());
                } else if (previous == null && next != null) {
                    next.setPrevious(null);
                } else if (previous != null && next == null) {
                    previous.setNext(null);
                }
                cleaned = playlist.getVideoItems().stream().filter(v -> !videoCode.equals(v.getCode())).toList();
            }
            playlist.setVideoItems(sortVideoItems(username, cleaned));
        }
        return Optional.of(playlist);
    }

    public Playlist movePlaylistVideoItem(String username, Long listId, String videoCode, String direction) {
        Playlist playlist = getPlaylist(username, listId).get();
        if (playlist.getVideoItems().size() < 2) {
            return playlist;
        }
        VideoItem currentNode = getVideoItem(playlist, videoCode);
        if (currentNode == null) {
            return playlist;
        }
        if (UP.equalsIgnoreCase(direction)) {
            if (StringUtils.hasText(currentNode.getPrevious())) {
                VideoItem currentPrevious = getVideoItem(playlist, currentNode.getPrevious());
                if (StringUtils.hasText(currentPrevious.getPrevious())) {
                    String newPreviousVideoCode = currentPrevious.getPrevious();
                    playlist = moveVideoItemUp(username, playlist, currentNode, newPreviousVideoCode);
                } else {
                    playlist = moveVideoItemUp(username, playlist, currentNode, TOP);
                }
            }
        } else if (DOWN.equalsIgnoreCase(direction)) {
            if (StringUtils.hasText(currentNode.getNext())) {
                String newPreviousVideoCode = currentNode.getNext();
                playlist = moveVideoItemDown(username, playlist, currentNode, newPreviousVideoCode);
            }
        }
        return playlist;
    }


    private Playlist moveVideoItemUp(String username, Playlist playlist, VideoItem current, String newPreviousVideoCode) {
        boolean moved = false;
        VideoItem previous = getVideoItem(playlist, current.getPrevious());
        VideoItem next = getVideoItem(playlist, current.getNext());
        VideoItem newPrevious = null;
        if (TOP.equals(newPreviousVideoCode)) {
            newPrevious = getFirstVideoItem(playlist);
        } else {
            newPrevious = getVideoItem(playlist, newPreviousVideoCode);
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
                VideoItem newNext = getVideoItem(playlist, newPrevious.getNext());
                newNext.setPrevious(current.getCode());
                newPrevious.setNext(current.getCode());
            }
            moved = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (moved) {
            playlist.setVideoItems(sortVideoItems(username, playlist.getVideoItems()));
        }
        return playlist;
    }

    private Playlist moveVideoItemDown(String username, Playlist playlist, VideoItem current, String newPreviousVideoCode) {
        boolean moved = false;
        VideoItem previous = getVideoItem(playlist, current.getPrevious());
        VideoItem next = getVideoItem(playlist, current.getNext());
        VideoItem newPrevious = null;
        if (BOTTOM.equals(newPreviousVideoCode)) {
            newPrevious = getLastVideoItem(playlist);
        } else {
            newPrevious = getVideoItem(playlist, newPreviousVideoCode);
        }
        try {
            if (previous == null) {
                next.setPrevious(null);
            } else {
                next.setPrevious(previous.getCode());
                previous.setNext(next.getCode());
            }
            VideoItem nextNext = null;
            if (newPrevious.getCode().equals(next.getCode())) {
                if (StringUtils.hasText(next.getNext())) {
                    nextNext = getVideoItem(playlist, next.getNext());
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
                    nextNext = getVideoItem(playlist, newPrevious.getNext());
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
            playlist.setVideoItems(sortVideoItems(username, playlist.getVideoItems()));
        }
        return playlist;
    }


    private VideoItem getVideoItem(Playlist playlist, String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return playlist.getVideoItems().stream().filter(v -> code.equals(v.getCode())).findAny().orElse(null);
    }

    private VideoItem getFirstVideoItem(Playlist playlist) {
        return playlist.getVideoItems().stream().filter(v -> !StringUtils.hasText(v.getPrevious())).findAny().orElse(null);
    }
    private VideoItem getLastVideoItem(Playlist playlist) {
        return playlist.getVideoItems().stream().filter(v -> !StringUtils.hasText(v.getNext())).findAny().orElse(null);
    }

    public boolean playlistExistsByName(String username, String playlistName) {
        if (!StringUtils.hasText(playlistName)) {
            return false;
        }
        if (PLAYLISTS.containsKey(username)) {
            return PLAYLISTS.get(username).stream().anyMatch(playlist -> playlistName.equals(playlist.getName()));
        }
        return false;
    }

    private List<VideoItem> sortVideoItems(String username, List<VideoItem> videoItems) {
        List<VideoItem> sortedList =  new ArrayList<>();
        if (videoItems == null || videoItems.isEmpty()) {
            return sortedList;
        }
        List<String> favoriteVideoCodes = getUserFavoriteVideoCodes(username);
        VideoItem videoItem = videoItems.stream().filter(v -> !StringUtils.hasText(v.getPrevious())).findAny().get();
        sortedList.add(videoItem);
        while (StringUtils.hasText(videoItem.getNext())) {
            String next = videoItem.getNext();;
            videoItem = videoItems.stream().filter(v -> next.equals(v.getCode())).findAny().get();
            videoItem.getVideo().setFavorite(favoriteVideoCodes.contains(videoItem.getVideo().getCode()));
            sortedList.add(videoItem);
        }
        return sortedList;
    }

}
