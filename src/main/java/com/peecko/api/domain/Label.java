package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.Lang;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Label {

    @Id
    private Long id;
    private String name;
    private Lang lang;
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
