package com.peecko.api.web.payload.response;

import com.peecko.api.domain.dto.VideoDTO;

import java.util.List;

public record TodayResponse(String greeting, List<VideoDTO> videos, List<String> tags) {
}
