package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.Lang;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "label")
public class Label implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Lang lang;

    @Column(name = "code")
    private String code;

    @Column(name = "text")
    private String text;

    public Label() {
    }

    public Label(Lang lang, String code, String text) {
        this.lang = lang;
        this.code = code;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
