package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "aps_device")
public class ApsDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "phone_model")
    private String phoneModel;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "installed_on")
    private Instant installedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aps_user_id")
    @JsonIgnoreProperties(value = { "apsDevices", "playlists" }, allowSetters = true)
    private ApsUser apsUser;

    public Long getId() {
        return this.id;
    }

    public ApsDevice id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public ApsDevice username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public ApsDevice deviceId(String deviceId) {
        this.setDeviceId(deviceId);
        return this;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhoneModel() {
        return this.phoneModel;
    }

    public ApsDevice phoneModel(String phoneModel) {
        this.setPhoneModel(phoneModel);
        return this;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getOsVersion() {
        return this.osVersion;
    }

    public ApsDevice osVersion(String osVersion) {
        this.setOsVersion(osVersion);
        return this;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public Instant getInstalledOn() {
        return this.installedOn;
    }

    public ApsDevice installedOn(Instant installedOn) {
        this.setInstalledOn(installedOn);
        return this;
    }

    public void setInstalledOn(Instant installedOn) {
        this.installedOn = installedOn;
    }

    public ApsUser getApsUser() {
        return this.apsUser;
    }

    public void setApsUser(ApsUser apsUser) {
        this.apsUser = apsUser;
    }

    public ApsDevice apsUser(ApsUser apsUser) {
        this.setApsUser(apsUser);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApsDevice device = (ApsDevice) o;
        return username.equals(device.username) && deviceId.equals(device.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, deviceId);
    }

}
