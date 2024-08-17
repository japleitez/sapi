package com.peecko.api.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class VideoItem {

    @Id
    private String code;

    @ManyToOne
    private VideoItem previous;

    @ManyToOne
    private VideoItem next;

    @ManyToOne
    private PlayList playlist;

    @OneToOne(mappedBy = "previousVideo")
    private VideoItem nextVideo;

    @OneToOne
    private VideoItem previousVideo;

    // Constructors, Getters, and Setters
    public VideoItem() {
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

    public PlayList getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlayList playlist) {
        this.playlist = playlist;
    }
}

