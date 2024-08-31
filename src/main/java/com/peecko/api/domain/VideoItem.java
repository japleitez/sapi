package com.peecko.api.domain;

import jakarta.persistence.*;

@Entity
public class VideoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_video_item_id")
    private VideoItem next;

    @OneToOne(fetch = FetchType.LAZY)
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

}

