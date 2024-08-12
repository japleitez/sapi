package com.peecko.api.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;

@Entity
@Table(name = "notification_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class NotificationItem implements Serializable {

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
    private Long NotificationId;

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
        return NotificationId;
    }

    public void setNotificationId(Long notificationId) {
        NotificationId = notificationId;
    }
}
