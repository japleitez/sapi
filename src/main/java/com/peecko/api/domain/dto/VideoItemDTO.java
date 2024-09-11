package com.peecko.api.domain.dto;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoItemDTO {
    private Integer index;
    private String previous;
    private String code;
    private String next;
    private VideoDTO video;
}
