package com.peecko.api.repository;

import com.peecko.api.domain.Category;
import com.peecko.api.domain.Video;
import com.peecko.api.utils.Common;
import com.peecko.api.utils.VideoLoader;
import org.apache.commons.text.CaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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



    public List<Video> getTodayVideos(String user) {
        loadVideos();
        return decorate(TODAY_VIDEOS, user);
    }

    public List<Category> getLibrary(String user) {
        loadVideos();
        return LIBRARY.stream().map(category -> decorate(category, user)).collect(Collectors.toList());
    }

    public Optional<Category> getCategory(String code, String user) {
        loadVideos();
        return CATEGORIES.stream().filter(category -> category.getCode().equals(code)).map(category -> decorate(category, user)).findFirst();
    }

    public List<Video> getUserFavorites(String user) {
        loadVideos();
        List<String> videoCodes = getUserVideoCodes(user);
        return ALL_VIDEOS.stream()
            .filter(video -> videoCodes.contains(video.getCode()))
            .map(video -> {
                Video clone = Common.clone(video);
                clone.setFavorite(true);
                return clone;
            })
            .collect(Collectors.toList());
    }

    public void addFavorite(String user, String code) {
        loadVideos();
        Optional<Video> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(code)).findFirst();
        if (optional.isPresent()) {
            Video video = optional.get();
            List<String> videoCodes = getUserVideoCodes(user);
            if (!videoCodes.contains(video.getCode())) {
                videoCodes.add(video.getCode());
                FAVORITES.put(user, videoCodes);
            }
        }
    }

    public void removeFavorite(String user, String code) {
        loadVideos();
        Optional<Video> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(code)).findFirst();
        if (optional.isPresent()) {
            Video video = optional.get();
            List<String> videoCodes = getUserVideoCodes(user);
            if (videoCodes.contains(video.getCode())) {
                videoCodes.remove(video.getCode());
                FAVORITES.put(user, videoCodes);
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

    private void loadVideos() {
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

    public static List<String> getUserVideoCodes(String user) {
        List<String> userFavorites = FAVORITES.get(user);
        if (userFavorites == null) {
            userFavorites = new LinkedList<>();
        }
        return userFavorites;
    }

    private List<Video> decorate(List<Video> videos, String user) {
        List<String> videoCodes = getUserVideoCodes(user);
        return videos.stream().map(video -> {
            Video clone = Common.clone(video);
            clone.setFavorite(videoCodes.contains(video.getCode()));
            return clone;
        }).collect(Collectors.toList());
    }

    private Category decorate(Category category, String user) {
        Category nc = new Category();
        nc.setCode(category.getCode());
        nc.setTitle(category.getTitle());
        nc.setVideos(decorate(category.getVideos(), user));
        return nc;
    }
}
