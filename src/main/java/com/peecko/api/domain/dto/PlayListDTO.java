package com.peecko.api.domain.dto;

import lombok.*;
import lombok.experimental.Accessors;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayListDTO {
    private String username;
    private Long id;
    private String name;
    private List<VideoItemDTO> videoItemDTOS = new ArrayList<>();
}
