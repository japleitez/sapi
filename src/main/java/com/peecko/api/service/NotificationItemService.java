package com.peecko.api.service;

import com.peecko.api.domain.NotificationItem;
import com.peecko.api.repository.NotificationItemRepo;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class NotificationItemService {

    private final NotificationItemRepo notificationItemRepo;

    public NotificationItemService(NotificationItemRepo notificationItemRepo) {
        this.notificationItemRepo = notificationItemRepo;
    }

    @Transactional
    public NotificationItem addNotificationItem(@NotNull Long apsUserId, @NotNull Long notificationId) {
        if (!notificationItemRepo.existsByApsUserIdAndNotificationId(apsUserId, notificationId)) {
            NotificationItem notificationItem = new NotificationItem();
            notificationItem.setApsUserId(apsUserId);
            notificationItem.setNotificationId(notificationId);
            return notificationItemRepo.save(notificationItem);
        } else {
            return null;
        }
    }

    @Transactional
    public void removeNotificationItem(Long apsUserId, Long notificationId) {
        notificationItemRepo.deleteByApsUserIdAndNotificationId(apsUserId, notificationId);
    }

}
