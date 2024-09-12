package com.peecko.api.domain.dto;

import lombok.*;

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
