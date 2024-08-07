package com.peecko.api.domain;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Setter
@Getter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Playlist {
    private String username;
    private Long id;
    private String name;
    private List<VideoItem> videoItems = new ArrayList<>();
}
