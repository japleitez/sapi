package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peecko.api.domain.enumeration.Lang;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "aps_user")
public class ApsUser implements Serializable {

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
    @Column(name = "username", nullable = false)
    private String username;

    @NotNull
    @Column(name = "username_verified", nullable = false)
    private Boolean usernameVerified;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Lang lang;

    @Column(name = "license")
    private String license;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "password")
    private String password;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "aps_user_id")
    @JsonIgnoreProperties(value = { "apsUser" }, allowSetters = true)
    private Set<ApsDevice> apsDevices = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "apsUser")
    @JsonIgnoreProperties(value = { "videoItems", "apsUser" }, allowSetters = true)
    private Set<PlayList> playLists = new HashSet<>();

    public Long getId() {
        return this.id;
    }

    public ApsUser() {
    }

    public ApsUser(Long id) {
        this.id = id;
    }

    public ApsUser(Long id, Lang lang) {
        this.id = id;
        this.lang = lang;
    }

    public static ApsUser of(Long id) {
        return new ApsUser(id);
    }

    public ApsUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public ApsUser name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return this.username;
    }

    public ApsUser username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getUsernameVerified() {
        return this.usernameVerified;
    }

    public ApsUser usernameVerified(Boolean usernameVerified) {
        this.setUsernameVerified(usernameVerified);
        return this;
    }

    public void setUsernameVerified(Boolean usernameVerified) {
        this.usernameVerified = usernameVerified;
    }

    public Lang getLanguage() {
        return this.lang;
    }

    public ApsUser language(Lang lang) {
        this.setLanguage(lang);
        return this;
    }

    public void setLanguage(Lang lang) {
        this.lang = lang;
    }

    public String getLicense() {
        return this.license;
    }

    public ApsUser license(String license) {
        this.setLicense(license);
        return this;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Boolean getActive() {
        return this.active;
    }

    public ApsUser active(Boolean active) {
        this.setActive(active);
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getPassword() {
        return this.password;
    }

    public ApsUser password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Instant getCreated() {
        return this.created;
    }

    public ApsUser created(Instant created) {
        this.setCreated(created);
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return this.updated;
    }

    public ApsUser updated(Instant updated) {
        this.setUpdated(updated);
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Set<ApsDevice> getApsDevices() {
        return this.apsDevices;
    }

    public void setApsDevices(Set<ApsDevice> apsDevices) {
        if (this.apsDevices != null) {
            this.apsDevices.forEach(i -> i.setApsUser(null));
        }
        if (apsDevices != null) {
            apsDevices.forEach(i -> i.setApsUser(this));
        }
        this.apsDevices = apsDevices;
    }

    public ApsUser apsDevices(Set<ApsDevice> apsDevices) {
        this.setApsDevices(apsDevices);
        return this;
    }

    public ApsUser addApsDevice(ApsDevice apsDevice) {
        this.apsDevices.add(apsDevice);
        apsDevice.setApsUser(this);
        return this;
    }

    public ApsUser removeApsDevice(ApsDevice apsDevice) {
        this.apsDevices.remove(apsDevice);
        apsDevice.setApsUser(null);
        return this;
    }

    public Set<PlayList> getPlaylists() {
        return this.playLists;
    }

    public void setPlaylists(Set<PlayList> playLists) {
        if (this.playLists != null) {
            this.playLists.forEach(i -> i.setApsUser(null));
        }
        if (playLists != null) {
            playLists.forEach(i -> i.setApsUser(this));
        }
        this.playLists = playLists;
    }

    public ApsUser playlists(Set<PlayList> playLists) {
        this.setPlaylists(playLists);
        return this;
    }

    public ApsUser addPlaylist(PlayList playList) {
        this.playLists.add(playList);
        playList.setApsUser(this);
        return this;
    }

    public ApsUser removePlaylist(PlayList playList) {
        this.playLists.remove(playList);
        playList.setApsUser(null);
        return this;
    }

    public Locale getLocale() {
        return lang != null? Locale.forLanguageTag(lang.name()): Locale.ENGLISH;
    }

    public ApsUser cloneID() {
        ApsUser user = new ApsUser();
        user.setId(id);
        return user;
    }

}
