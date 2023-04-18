package com.peecko.api.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Setter @Getter @Accessors(chain = true)
public class Video {
    private String code;
    private String title;
    private String category;
    private String coach;
    private String duration;
    private String url;
    private List<String> tags;

}
