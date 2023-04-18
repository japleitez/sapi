package com.peecko.api.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter @Getter
public class Category {
    private String code;
    private String title;
    private List<Video> videos;
}
