package com.peecko.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "aps_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApsOrder implements Serializable {

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
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    @NotNull
    @Column(name = "vat_rate", nullable = false)
    private Double vatRate;

    @Column(name = "number_of_users", nullable = false)
    private Integer numberOfUsers;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "apsOrder")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "apsOrder" }, allowSetters = true)
    private Set<ApsMembership> apsMemberships = new HashSet<>();

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "country")
    private String country;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public ApsOrder() {
    }

    public ApsOrder(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public ApsOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPeriod() {
        return this.period;
    }

    public ApsOrder period(Integer period) {
        this.setPeriod(period);
        return this;
    }

    public void setPeriod(Integer period) {
        this.period = period;
    }

    public String getLicense() {
        return this.license;
    }

    public ApsOrder license(String license) {
        this.setLicense(license);
        return this;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Double getUnitPrice() {
        return this.unitPrice;
    }

    public ApsOrder unitPrice(Double unitPrice) {
        this.setUnitPrice(unitPrice);
        return this;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getVatRate() {
        return this.vatRate;
    }

    public ApsOrder vatRate(Double vatRate) {
        this.setVatRate(vatRate);
        return this;
    }

    public void setVatRate(Double vatRate) {
        this.vatRate = vatRate;
    }

    public Integer getNumberOfUsers() {
        return this.numberOfUsers;
    }

    public ApsOrder numberOfUsers(Integer numberOfUsers) {
        this.setNumberOfUsers(numberOfUsers);
        return this;
    }

    public void setNumberOfUsers(Integer numberOfUsers) {
        this.numberOfUsers = numberOfUsers;
    }

    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public ApsOrder invoiceNumber(String invoiceNumber) {
        this.setInvoiceNumber(invoiceNumber);
        return this;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Set<ApsMembership> getApsMemberships() {
        return this.apsMemberships;
    }

    public void setApsMemberships(Set<ApsMembership> apsMemberships) {
        if (this.apsMemberships != null) {
            this.apsMemberships.forEach(i -> i.setApsOrder(null));
        }
        if (apsMemberships != null) {
            apsMemberships.forEach(i -> i.setApsOrder(this));
        }
        this.apsMemberships = apsMemberships;
    }

    public ApsOrder apsMemberships(Set<ApsMembership> apsMemberships) {
        this.setApsMemberships(apsMemberships);
        return this;
    }

    public ApsOrder addApsMembership(ApsMembership apsMembership) {
        this.apsMemberships.add(apsMembership);
        apsMembership.setApsOrder(this);
        return this;
    }

    public ApsOrder removeApsMembership(ApsMembership apsMembership) {
        this.apsMemberships.remove(apsMembership);
        apsMembership.setApsOrder(null);
        return this;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public ApsOrder customerId(Long customerId) {
        this.setCustomerId(customerId);
        return this;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public ApsOrder country (String country) {
        this.setCountry(country);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApsOrder)) {
            return false;
        }
        return getId() != null && getId().equals(((ApsOrder) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    public boolean hasSubscribers() {
        return Objects.nonNull(this.numberOfUsers) && this.numberOfUsers > 0;
    }

}
