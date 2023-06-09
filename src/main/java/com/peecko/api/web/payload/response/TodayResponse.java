package com.peecko.api.web.payload.response;

import com.peecko.api.domain.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor @Setter @Getter
public class TodayResponse {
    private String greeting;
    private List<String> tags;
    private List<Video> videos;
}
