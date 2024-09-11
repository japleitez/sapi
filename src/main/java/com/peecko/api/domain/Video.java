package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peecko.api.domain.enumeration.Intensity;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.enumeration.Player;
import jakarta.persistence.*;

import java.time.Instant;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "video")
public class Video implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "duration")
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Lang lang;

    @Column(name = "tags")
    private String tags;

    @Enumerated(EnumType.STRING)
    @Column(name = "player", nullable = false)
    private Player player;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "url")
    private String url;

    @Column(name = "audience")
    private String audience;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity")
    private Intensity intensity;

    @Column(name = "filename")
    private String filename;

    @Column(name = "description")
    private String description;

    @Column(name = "released")
    private LocalDate released;

    @Column(name = "archived")
    private LocalDate archived;

    @Column(name = "created")
    private Instant created;

    LocalDate publicationDate;

    public int selectionCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "videos" }, allowSetters = true)
    private VideoCategory videoCategory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "videos", "articles" }, allowSetters = true)
    private Coach coach;

    @Transient
    private boolean favorite;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Lang getLanguage() {
        return lang;
    }

    public void setLanguage(Lang lang) {
        this.lang = lang;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public Intensity getIntensity() {
        return intensity;
    }

    public void setIntensity(Intensity intensity) {
        this.intensity = intensity;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public LocalDate getReleased() { return this.released; }

    public void setReleased(LocalDate released) {
        this.released = released;
    }

    public LocalDate getArchived() {
        return archived;
    }

    public void setArchived(LocalDate archived) {
        this.archived = archived;
    }

    public VideoCategory getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(VideoCategory videoCategory) {
        this.videoCategory = videoCategory;
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }


    public void incrementSelectionCount() {
        this.selectionCount++;
    }

    public long getDaysSincePublication() {
        return ChronoUnit.DAYS.between(publicationDate, LocalDate.now());
    }
}
