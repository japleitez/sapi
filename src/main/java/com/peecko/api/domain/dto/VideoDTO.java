package com.peecko.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Setter @Getter @Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VideoDTO {
    @EqualsAndHashCode.Include
    private String code;
    private String category;
    private String title;
    private String duration;
    private String coach;
    @JsonProperty("coach-website")
    private String coachWebsite;
    @JsonProperty("coach-email")
    private String coachEmail;
    @JsonProperty("coach-instagram")
    private String coachInstagram;
    private String image;
    private String url;
    private String audience;
    private String intensity;
    private List<String> tags;
    private String description;
    private String resume;
    private String player;
    private boolean favorite;
}
