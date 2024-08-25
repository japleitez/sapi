package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.Lang;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
public class TodayVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Lang language;

    private LocalDate releaseDate;

    @ElementCollection
    @CollectionTable(name = "today_video_video_ids", joinColumns = @JoinColumn(name = "today_video_id"))
    @Column(name = "video_id")
    private Set<Long> videoIds;

    public TodayVideo() {}

    public TodayVideo(LocalDate releaseDate, Set<Long> videoIds) {
        this.releaseDate = releaseDate;
        this.videoIds = videoIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lang getLanguage() {
        return language;
    }

    public void setLanguage(Lang language) {
        this.language = language;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Set<Long> getVideoIds() {
        return videoIds;
    }

    public void setVideoIds(Set<Long> videoIds) {
        this.videoIds = videoIds;
    }
}
