package com.peecko.api.domain.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private String code;
    private String title;
    private List<VideoDTO> videos = new ArrayList<>();
}
