package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ApsMembership.
 */
@Entity
@Table(name = "aps_membership")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApsMembership implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "period", nullable = false)
    private Integer period;

    @NotNull
    @Column(name = "license", nullable = false)
    private String license;

    @NotNull
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return this.id;
    }

    public ApsMembership id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPeriod() {
        return this.period;
    }

    public ApsMembership period(Integer period) {
        this.setPeriod(period);
        return this;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getLicense() {
        return this.license;
    }

    public ApsMembership license(String license) {
        this.setLicense(license);
        return this;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getUsername() {
        return this.username;
    }

    public ApsMembership username(String username) {
        this.setUsername(username);
        return this;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public ApsMembership customerId(Long customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApsMembership)) {
            return false;
        }
        return getId() != null && getId().equals(((ApsMembership) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ApsMembership{" +
            "id=" + getId() +
            ", period=" + getPeriod() +
            ", license='" + getLicense() + "'" +
            ", username='" + getUsername() + "'" +
            "}";
    }
}
