package com.peecko.api.repository;

import com.peecko.api.domain.Category;
import com.peecko.api.domain.Video;
import com.peecko.api.utils.VideoLoader;
import org.apache.commons.text.CaseUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class VideoRepository {

    private static final List<Video> TODAY_VIDEOS = new LinkedList<>();
    private static final List<Category> CATEGORIES = new LinkedList<>();
    private static final List<Category> LIBRARY = new LinkedList<>();
    public static final HashMap<String, List<Video>> FAVORITES =  new LinkedHashMap<>();
    public static final Set<Video> ALL_VIDEOS = new HashSet<>();

    static {
        loadVideos();
    }

    public List<Video> getTodayVideos() {
        return TODAY_VIDEOS;
    }

    public List<Category> getLibrary() {
        return LIBRARY;
    }

    public Optional<Category> getCategory(String code) {
        return CATEGORIES.stream().filter(category -> category.getCode().equals(code)).findFirst();
    }

    public List<Video> getUserFavorites(String user) {
        List<Video> userFavorites = FAVORITES.get(user);
        if (userFavorites == null) {
            userFavorites = new LinkedList<>();
        }
        return userFavorites;
    }

    public void addFavorite(String user, String code) {
        Optional<Video> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(code)).findFirst();
        if (optional.isPresent()) {
            Video video = optional.get();
            List<Video> userFavorites = getUserFavorites(user);
            if (!userFavorites.contains(video)) {
                userFavorites.add(video);
                FAVORITES.put(user, userFavorites);
            }
        }
    }

    public void removeFavorite(String user, String code) {
        Optional<Video> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(code)).findFirst();
        if (optional.isPresent()) {
            Video video = optional.get();
            List<Video> userFavorites = getUserFavorites(user);
            if (userFavorites.contains(video)) {
                userFavorites.remove(video);
                FAVORITES.put(user, userFavorites);
            }
        }
    }

    private static void loadVideos() {
        List<String> vc = List.of("YOGA", "PILATES", "CALISTHENICS", "MEDITATION", "HEALTH RISK");
        ALL_VIDEOS.addAll(new VideoLoader().loadVideos("/data/videos.csv"));
        for(String categoryName: vc) {
            Category ca1 = new Category();
            ca1.setCode(categoryName.substring(0,2).toLowerCase());
            ca1.setTitle(CaseUtils.toCamelCase(categoryName, true, null));
            ca1.setVideos(videosByCategory(categoryName));
            CATEGORIES.add(ca1);
            TODAY_VIDEOS.add(ca1.getVideos().get(0));
            Category ca2 = new Category();
            ca2.setCode(ca1.getCode());
            ca2.setTitle(ca1.getTitle());
            ca2.setVideos(copyVideos(ca1.getVideos(), 3));
            LIBRARY.add(ca2);
        }
    }

    private static List<Video> videosByCategory(String category) {
        return new ArrayList<>(ALL_VIDEOS.stream().filter(v -> v.getCategory().equals(category)).collect(Collectors.toList()));
    }

    private static List<Video> copyVideos(List<Video> from, int num) {
        List<Video> list = new LinkedList<>();
        for(int i = 0; i < num; i++) {
            list.add(from.get(i));
        }
        return list;
    }

}
