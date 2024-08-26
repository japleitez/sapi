package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.Help;
import com.peecko.api.domain.dto.NotificationDTO;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.domain.mapper.HelpItemMapper;
import com.peecko.api.domain.mapper.NotificationMapper;
import com.peecko.api.repository.*;
import com.peecko.api.utils.Common;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService {

    final ApsUserRepo apsUserRepo;
    final NotificationRepo notificationRepo;
    final ApsMembershipRepo apsMembershipRepo;
    final NotificationItemRepo notificationItemRepo;
    final NotificationItemService notificationItemService;
    final LabelRepo labelRepo;
    final HelpItemRepo helpItemRepo;

    public AccountService(ApsUserRepo apsUserRepo, NotificationItemRepo notificationItemRepo, NotificationRepo notificationRepo, ApsMembershipRepo apsMembershipRepo, NotificationItemService notificationItemService, LabelRepo labelRepo, HelpItemRepo helpItemRepo) {
        this.apsUserRepo = apsUserRepo;
        this.notificationRepo = notificationRepo;
        this.apsMembershipRepo = apsMembershipRepo;
        this.notificationItemRepo = notificationItemRepo;
        this.notificationItemService = notificationItemService;
        this.labelRepo = labelRepo;
        this.helpItemRepo = helpItemRepo;
    }

    public boolean activateUserLicense(String username, Integer period, String license) {
        boolean activated = apsMembershipRepo.existsByUsernameAndPeriodAndLicense(username, period, license);
        if (activated) {
            apsUserRepo.setLicense(username, license);
        }
        return activated;
    }

    public List<NotificationDTO> getNotifications(String username) {
        //TODO need to improve performance by having today's notifications already selected by customer or license
        //TODO the precooked data should be done by the backoffice application
        ApsUser apsUser = apsUserRepo.findByUsername(username).orElse(null);
        if (apsUser == null) {
            return List.of();
        }
        ApsMembership membership = apsMembershipRepo.findByUsernameAndPeriod(username, Common.currentPeriod()).orElse(null);
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
        return notificationItemRepo
                .findByApsUserIdAndNotificationIdIn(apsUserId, notificationIds)
                .stream()
                .map(NotificationItem::getNotificationId)
                .collect(Collectors.toSet());
    }

    public void addNotificationItem(Long apsUserId, Long notificationId) {
        notificationItemService.addNotificationItem(apsUserId, notificationId);
    }

    public List<Help> findHelpByLang(Lang lang) {
        return helpItemRepo.findByLang(lang).stream().map(HelpItemMapper::help).toList();
    }

}
