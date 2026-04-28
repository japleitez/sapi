package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class PlayListItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "play_list_id", nullable = false)
    @JsonIgnore
    private PlayList playList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "position", nullable = false)
    private int position;

    @Column(name = "code", nullable = false)
    private String code;

    public PlayListItem() {
    }

    public PlayListItem(PlayList playList, Video video) {
        this.video = video;
        this.playList = playList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlayList getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PlayListItem video(Video video) {
        this.video = video;
        return this;
    }

    public PlayListItem playList(PlayList playList) {
        this.playList = playList;
        return this;
    }

    public PlayListItem code(String code) {
        this.code = code;
        return this;
    }

    public PlayListItem position(int position) {
        this.position = position;
        return this;
    }

}

