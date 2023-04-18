package com.peecko.api.web.rest;

import com.peecko.api.domain.User;
import com.peecko.api.domain.Video;
import com.peecko.api.repository.TodayRepository;
import com.peecko.api.repository.UserRepository;
import com.peecko.api.web.payload.request.ActivationRequest;
import com.peecko.api.web.payload.response.MessageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/today")
public class TodayController {

    final TodayRepository todayRepository;

    public TodayController(TodayRepository todayRepository) {
        this.todayRepository = todayRepository;
    }

    @GetMapping("/videos")
    public ResponseEntity<?> getVideos() {
        List<Video> videos = todayRepository.getVideos();
        return ResponseEntity.ok(videos);
    }

}
