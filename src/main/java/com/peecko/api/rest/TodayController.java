package com.peecko.api.rest;

import com.peecko.api.domain.dto.VideoDTO;
import com.peecko.api.service.LabelService;
import com.peecko.api.service.TodayService;
import com.peecko.api.utils.Common;
import com.peecko.api.web.payload.response.TodayResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class TodayController {

    private final TodayService todayService;
    private final LabelService labelService;

    public TodayController(TodayService todayService, LabelService labelService) {
        this.todayService = todayService;
        this.labelService = labelService;
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayVideos() {
        String greeting = labelService.getLabel("greeting");
        Long userId = 1L;
        List<VideoDTO> videos = todayService.getTodayVideos(userId);
        List<String> tags = Common.getVideoTags(videos);
        return ResponseEntity.ok(new TodayResponse(greeting, tags, videos));
    }

}
