package com.peecko.api.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Setter @Getter @Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Video {
    @EqualsAndHashCode.Include
    private String code;
    private String category;
    private String title;
    private String duration;
    private String coach;
    private String image;
    private String url;
    private String audience;
    private String intensity;
    private List<String> tags;
    private String description;
    private String resume;
    private String player;
}
