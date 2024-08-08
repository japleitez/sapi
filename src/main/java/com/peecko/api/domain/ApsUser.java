package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.peecko.api.domain.enumeration.Language;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
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

    @Column(name = "name")
    private String name;

    @Column(name = "username")
    private String username;

    @Column(name = "username_verified")
    private Boolean usernameVerified;

    @Column(name = "private_email")
    private String privateEmail;

    @Column(name = "private_verified")
    private Boolean privateVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    @Column(name = "license")
    private String license;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "password")
    private String password;

    @Column(name = "created")
    private Instant created;

    @Column(name = "updated")
    private Instant updated;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "apsUser")
    @JsonIgnoreProperties(value = { "apsUser" }, allowSetters = true)
    private Set<ApsDevice> apsDevices = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "apsUser")
    @JsonIgnoreProperties(value = { "videoItems", "apsUser" }, allowSetters = true)
    private Set<PlayList> playLists = new HashSet<>();

    public Long getId() {
        return this.id;
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

    public String getPrivateEmail() {
        return this.privateEmail;
    }

    public ApsUser privateEmail(String privateEmail) {
        this.setPrivateEmail(privateEmail);
        return this;
    }

    public void setPrivateEmail(String privateEmail) {
        this.privateEmail = privateEmail;
    }

    public Boolean getPrivateVerified() {
        return this.privateVerified;
    }

    public ApsUser privateVerified(Boolean privateVerified) {
        this.setPrivateVerified(privateVerified);
        return this;
    }

    public void setPrivateVerified(Boolean privateVerified) {
        this.privateVerified = privateVerified;
    }

    public Language getLanguage() {
        return this.language;
    }

    public ApsUser language(Language language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(Language language) {
        this.language = language;
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

    public Set<PlayList> getPlayLists() {
        return this.playLists;
    }

    public void setPlayLists(Set<PlayList> playLists) {
        if (this.playLists != null) {
            this.playLists.forEach(i -> i.setApsUser(null));
        }
        if (playLists != null) {
            playLists.forEach(i -> i.setApsUser(this));
        }
        this.playLists = playLists;
    }

    public ApsUser playLists(Set<PlayList> playLists) {
        this.setPlayLists(playLists);
        return this;
    }

    public ApsUser addPlayList(PlayList playList) {
        this.playLists.add(playList);
        playList.setApsUser(this);
        return this;
    }

    public ApsUser removePlayList(PlayList playList) {
        this.playLists.remove(playList);
        playList.setApsUser(null);
        return this;
    }
}
