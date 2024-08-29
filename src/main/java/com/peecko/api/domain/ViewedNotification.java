package com.peecko.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "notification_item", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"apsUserId", "notificationId"})
})
public class ViewedNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")

    private Long id;

    @NotNull
    @Column(name = "aps_user_id", nullable = false)
    private Long apsUserId;

    @NotNull
    @Column(name = "notification_id", nullable = false)
    private Long notificationId;

    @Column(name = "viewed_at", nullable = false)
    private Instant viewedAt;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApsUserId() {
        return apsUserId;
    }

    public void setApsUserId(Long apsUserId) {
        this.apsUserId = apsUserId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public Instant getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(Instant viewedAt) {
        this.viewedAt = viewedAt;
    }

}
