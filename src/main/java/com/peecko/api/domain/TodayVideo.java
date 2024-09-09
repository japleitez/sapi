package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.Lang;
import jakarta.persistence.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "today_video")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TodayVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Lang language;

    private LocalDate releaseDate;

    @ElementCollection
    @CollectionTable(name = "today_video_set", joinColumns = @JoinColumn(name = "today_video_id"))
    @Column(name = "video_id")
    private Set<Long> videoIds = new HashSet<>();

    public TodayVideo() {}

    public TodayVideo(Lang language, LocalDate releaseDate, Set<Long> videoIds) {
        this.language = language;
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
