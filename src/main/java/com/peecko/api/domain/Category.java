package com.peecko.api.domain;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter @Accessors(chain = true)
public class Category {
    private String code;
    private String title;
    private List<Video> videos;
}
