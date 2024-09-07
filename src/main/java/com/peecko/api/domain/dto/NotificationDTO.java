package com.peecko.api.domain.dto;

import lombok.*;
import lombok.experimental.Accessors;

@Data
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private String image;
    private String video;
    private String date;
    private Boolean viewed = false;

    public boolean isViewed() {
        return false;
    }
}
