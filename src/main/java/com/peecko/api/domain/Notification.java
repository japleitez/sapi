package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peecko.api.domain.enumeration.Language;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "message", nullable = false)
    private String message;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "starts")
    private LocalDate starts;

    @Column(name = "expires")
    private LocalDate expires;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(value = { "contacts", "apsPlans", "agency" }, allowSetters = true)
    private Customer customer;

    public Long getId() {
        return this.id;
    }

    public Notification id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Notification customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public void setCustomer(Customer company) {
        this.customer = company;
    }

    public String getTitle() {
        return this.title;
    }

    public Notification title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return this.message;
    }

    public Notification message(String message) {
        this.setMessage(message);
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Language getLanguage() {
        return this.language;
    }

    public Notification language(Language language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Notification imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return this.videoUrl;
    }

    public Notification videoUrl(String videoUrl) {
        this.setVideoUrl(videoUrl);
        return this;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public LocalDate getStarts() {
        return this.starts;
    }

    public Notification starts(LocalDate starts) {
        this.setStarts(starts);
        return this;
    }

    public void setStarts(LocalDate starts) {
        this.starts = starts;
    }

    public LocalDate getExpires() {
        return this.expires;
    }

    public Notification expires(LocalDate expires) {
        this.setExpires(expires);
        return this;
    }

    public void setExpires(LocalDate expires) {
        this.expires = expires;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return getId() != null && getId().equals(((Notification) o).getId());
    }

}
