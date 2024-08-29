package com.peecko.api.service;

import com.peecko.api.domain.ApsMembership;
import com.peecko.api.domain.ApsUser;
import com.peecko.api.domain.Notification;
import com.peecko.api.domain.ViewedNotification;
import com.peecko.api.domain.dto.NotificationDTO;
import com.peecko.api.domain.mapper.NotificationMapper;
import com.peecko.api.repository.ApsMembershipRepo;
import com.peecko.api.repository.ApsUserRepo;
import com.peecko.api.repository.ViewedNotificationRepo;
import com.peecko.api.repository.NotificationRepo;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    final ApsUserRepo apsUserRepo;
    final ApsMembershipRepo apsMembershipRepo;
    final NotificationRepo notificationRepo;
    final ViewedNotificationRepo viewedNotificationRepo;

    public NotificationService(ApsUserRepo apsUserRepo, ApsMembershipRepo apsMembershipRepo, NotificationRepo notificationRepo, ViewedNotificationRepo viewedNotificationRepo) {
        this.apsUserRepo = apsUserRepo;
        this.apsMembershipRepo = apsMembershipRepo;
        this.notificationRepo = notificationRepo;
        this.viewedNotificationRepo = viewedNotificationRepo;
    }

    @Transactional
    public void addViewedNotification(@NotNull Long apsUserId, @NotNull Long notificationId) {
        viewedNotificationRepo.findByApsUserIdAndNotificationId(apsUserId, notificationId).orElseGet(() -> {
            ViewedNotification viewedNotification = new ViewedNotification();
            viewedNotification.setApsUserId(apsUserId);
            viewedNotification.setNotificationId(notificationId);
            viewedNotification.setViewedAt(Instant.now());
            return viewedNotificationRepo.save(viewedNotification);
        });
    }

    public List<NotificationDTO> getNotifications(ApsUser apsUser, int period) {
        ApsMembership membership = apsMembershipRepo.findByUsernameAndPeriod(apsUser.getUsername(), period).orElse(null);
        if (membership == null) {
            return List.of();
        }
        List<Notification> notifications = notificationRepo.findByCustomerIdAndForToday(membership.getCustomerId(), LocalDate.now());
        if (notifications == null) {
            return List.of();
        }
        List<Long> notificationIds = notifications.stream().map(Notification::getId).toList();
        Set<Long> viewedIds = getViewedNotificationIds(apsUser.getId(), notificationIds);
        return notifications.stream().map(n -> NotificationMapper.notificationDTO(n, viewedIds)).toList();
    }

    private Set<Long> getViewedNotificationIds(Long apsUserId, List<Long> notificationIds) {
        return viewedNotificationRepo
                .findByApsUserIdAndNotificationIdIn(apsUserId, notificationIds)
                .stream()
                .map(ViewedNotification::getNotificationId)
                .collect(Collectors.toSet());
    }

}
