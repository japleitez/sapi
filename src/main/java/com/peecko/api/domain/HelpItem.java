package com.peecko.api.domain;

import com.peecko.api.domain.enumeration.Lang;
import jakarta.persistence.*;

@Entity
@Table(name = "help_item")
public class HelpItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Lang lang;

    @Column(name = "question")
    private String question;

    @Column(name = "answer")
    private String answer;

    public HelpItem() {
    }

    public HelpItem(Lang lang, String question, String answer) {
        this.lang = lang;
        this.question = question;
        this.answer = answer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Lang getLang() {
        return lang;
    }

    public void setLang(Lang lang) {
        this.lang = lang;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
