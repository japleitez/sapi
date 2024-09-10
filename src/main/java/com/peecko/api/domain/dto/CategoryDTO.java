package com.peecko.api.domain.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter @Accessors(chain = true)
public class CategoryDTO {
    private String code;
    private String title;
    private List<VideoDTO> videos = new ArrayList<>();
}
