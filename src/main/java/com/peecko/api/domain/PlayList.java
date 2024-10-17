package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "play_list")
public class PlayList implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "counter", nullable = false)
    private Integer counter;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "apsDevices", "playLists" }, allowSetters = true)
    private ApsUser apsUser;

    @OneToMany(mappedBy = "playList", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnoreProperties(value = { "playList" }, allowSetters = true)
    private List<VideoItem> videoItems = new ArrayList<>();


    public PlayList() {
    }

    public PlayList(Long id) {
        this.id = id;
    }

    public static PlayList of(Long id) {
        return new PlayList(id);
    }

    public Long getId() {
        return this.id;
    }

    public PlayList id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public PlayList name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCounter() {
        return this.counter;
    }

    public PlayList counter(Integer counter) {
        this.setCounter(counter);
        return this;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Instant getCreated() {
        return this.created;
    }

    public PlayList created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public PlayList updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public ApsUser getApsUser() {
        return this.apsUser;
    }

    public void setApsUser(ApsUser apsUser) {
        this.apsUser = apsUser;
    }

    public PlayList apsUser(ApsUser apsUser) {
        this.setApsUser(apsUser);
        return this;
    }

    public List<VideoItem> getVideoItems() {
        return videoItems;
    }

    public void setVideoItems(List<VideoItem> videoItems) {
        this.videoItems = videoItems;
    }

    public void addVideoItem(VideoItem videoItem) {
        videoItems.add(videoItem);
        videoItem.setPlayList(this);
    }

    public void removeVideoItem(VideoItem videoItem) {
        videoItems.remove(videoItem);
        videoItem.setPlayList(null);
    }

}
