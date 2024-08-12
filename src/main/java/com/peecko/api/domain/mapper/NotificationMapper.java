package com.peecko.api.domain.mapper;

import com.peecko.api.domain.Notification;
import com.peecko.api.domain.dto.NotificationDTO;
import com.peecko.api.utils.Common;

import java.util.List;
import java.util.Set;

public class NotificationMapper {

    public static NotificationDTO notificationDTO(Notification notification, Set<Long> viewedIds) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setImage(notification.getImageUrl());
        dto.setVideo(notification.getVideoUrl());
        dto.setDate(Common.localDateAsString(notification.getStarts()));
        dto.setViewed(viewedIds.contains(notification.getId()));
        return dto;
    }

}
