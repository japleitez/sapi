package com.peecko.api.domain;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Setter
@Getter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class VideoItem {
    private Integer index;
    private String previous;
    private String code;
    private String next;
    private Video video;
}
