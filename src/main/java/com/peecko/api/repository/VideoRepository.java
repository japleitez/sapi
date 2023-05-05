package com.peecko.api.repository;

import com.peecko.api.domain.Category;
import com.peecko.api.domain.Video;
import com.peecko.api.utils.VideoLoader;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class VideoRepository {

    private static final List<Video> TODAY_VIDEOS = new ArrayList<>();
    private static final List<Category> CATEGORIES = new ArrayList<>();
    private static final List<CategoryData> CATEGORY_DATA = new ArrayList<>();

    public static final HashMap<String, List<Video>> FAVORITES =  new HashMap<>();

    public static final Set<Video> ALL_VIDEOS = new HashSet<>();

    static {
        CATEGORY_DATA.add(new CategoryData("C1", "Health Risks", "/data/today.csv"));
        CATEGORY_DATA.add(new CategoryData("C2", "Yoga", "/data/today.csv"));
        CATEGORY_DATA.add(new CategoryData("C3", "Pilates", "/data/today.csv"));
        CATEGORY_DATA.add(new CategoryData("C4", "Calisthenics", "/data/today.csv"));
    }

    public List<Video> getTodayVideos() {
        loadVideos();
        return TODAY_VIDEOS;
    }

    private void loadVideos()  {
        if (TODAY_VIDEOS.isEmpty()) {
            TODAY_VIDEOS.addAll(new VideoLoader().loadVideos(Integer.MAX_VALUE, "/data/today.csv"));
        }
    }
    public List<Category> getCategories() {
        loadCategories();
        return CATEGORIES;
    }

    private void loadCategories() {
        if (CATEGORIES.isEmpty()) {
            CATEGORIES.addAll(CATEGORY_DATA.stream().map(this::loadCategory).toList());
        }
    }

    public Optional<Category> getCategory(String code) {
        return CATEGORY_DATA
            .stream()
            .filter(e -> code.equals(e.code))
            .findFirst()
            .map(this::loadCategory);
    }

    private Category loadCategory(CategoryData entry) {
        Category category = new Category();
        category.setCode(entry.code);
        category.setTitle(entry.title);
        category.setVideos(new ArrayList<>(new VideoLoader().loadVideos(3, entry.filename)));
        ALL_VIDEOS.addAll(category.getVideos());
        return category;
    }

    public void addFavorite(String user, String code) {
        Optional<Video> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(code)).findFirst();
        if (optional.isPresent()) {
            List<Video> userFavorites = FAVORITES.get(user);
            if (userFavorites == null) {
                userFavorites = new ArrayList<>();
            }
            userFavorites.add(optional.get());
            FAVORITES.put(user, userFavorites);
        }
    }

    public void removeFavorite(String user, String code) {
        Optional<Video> optional = ALL_VIDEOS.stream().filter(video -> video.getCode().equals(code)).findFirst();
        if (optional.isPresent()) {
            List<Video> userFavorites = FAVORITES.get(user);
            if (userFavorites != null) {
                userFavorites.remove(optional.get());
                FAVORITES.put(user, userFavorites);
            }
        }
    }
}
