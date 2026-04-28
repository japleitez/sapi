package com.peecko.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayListDTO {
    private String username;
    private Long id;
    private String name;
    private Integer counter;
    @JsonProperty("videoItems")
    private List<VideoItemDTO> videoItemDTOS = new ArrayList<>();
}
