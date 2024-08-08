package com.peecko.api.domain;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "user_favorite_video")
public class UserFavoriteVideo implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "aps_user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
}
