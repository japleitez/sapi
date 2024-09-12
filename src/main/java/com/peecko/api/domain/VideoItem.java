package com.peecko.api.domain;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class VideoItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "previous_video_item_id")
    private VideoItem next;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "next_video_item_id")
    private VideoItem previous;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    private PlayList playList;

    public VideoItem() {
    }

    public VideoItem(String code, PlayList playList) {
        this.code = code;
        this.playList = playList;
    }

    public VideoItem(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VideoItem getPrevious() {
        return previous;
    }

    public void setPrevious(VideoItem previous) {
        this.previous = previous;
    }

    public VideoItem getNext() {
        return next;
    }

    public void setNext(VideoItem next) {
        this.next = next;
    }

    public PlayList getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

