package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
public class VideoItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "code", referencedColumnName = "code", nullable = false)
    private Video video;

    @OneToOne
    @JoinColumn(name = "next_video_item_id")
    @JsonIgnore // prevent recursion in potential serialization
    private VideoItem next;


    @OneToOne
    @JoinColumn(name = "previous_video_item_id")
    @JsonIgnore // prevent recursion in potential serialization
    private VideoItem previous;

    @ManyToOne
    @JoinColumn(name = "play_list_id", nullable = false)
    @JsonIgnore
    private PlayList playList;

    public VideoItem() {
    }

    public VideoItem(PlayList playList, Video video) {
        this.video = video;
        this.playList = playList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public VideoItem getNext() {
        return next;
    }

    public void setNext(VideoItem next) {
        this.next = next;
    }

    public VideoItem getPrevious() {
        return previous;
    }

    public void setPrevious(VideoItem previous) {
        this.previous = previous;
    }

    public PlayList getPlayList() {
        return playList;
    }

    public void setPlayList(PlayList playList) {
        this.playList = playList;
    }

    public String getCode() {
        return video.getCode();
    }

}

