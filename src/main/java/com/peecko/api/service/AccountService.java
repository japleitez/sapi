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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        boolean activated = false;
        Optional<ApsMembership> optApsMembership = apsMembershipRepo.findByUsernameAndPeriodAndLicense(username, period, license);
        if (optApsMembership.isPresent()) {
            Optional<ApsUser> optApsUser = apsUserRepo.findByUsername(username);
            if (optApsUser.isPresent()) {
                ApsUser apsUser = optApsUser.get();
                apsUser.license(license);
                apsUserRepo.save(apsUser);
                activated = true;
            }
        }
        return activated;
    }

    public List<NotificationDTO> getNotifications(String username) {
        Optional<ApsUser> optUser = apsUserRepo.findByUsername(username);
        if (optUser.isPresent()) {
            ApsUser apsUser = optUser.get();
            Optional<ApsMembership> optMembership = apsMembershipRepo.findByUsernameAndPeriod(username, Common.currentPeriod());
            if (optMembership.isPresent()) {
                Customer customer = Customer.of(optMembership.get().getCustomerId());
                List<Notification> notifications = getActiveNotificationsForCustomer(customer);
                if (!notifications.isEmpty()) {
                    List<Long> notificationIds = notifications.stream().map(Notification::getId).toList();
                    Set<Long> viewedIds = getViewedNotificationIdsForApsUser(apsUser.getId(), notificationIds);
                    return notifications.stream().map(n -> NotificationMapper.notificationDTO(n, viewedIds)).toList();
                }
            }
        }
        return new ArrayList<>();
     }

    public List<Notification> getActiveNotificationsForCustomer(Customer customer) {
        LocalDate today = LocalDate.now();
        return notificationRepo.findByCustomerAndExpiresAfter(customer, today);
    }

    public Set<Long> getViewedNotificationIdsForApsUser(Long apsUserId, List<Long> notificationIds) {
        List<NotificationItem> notificationItems = notificationItemRepo.findByApsUserIdAndNotificationIdIn(apsUserId, notificationIds);
        return notificationItems.stream()
                .map(NotificationItem::getNotificationId)
                .collect(Collectors.toSet());
    }

    public void addNotificationItem(Long apsUserId, Long notificationId) {
        if (apsUserId != null && notificationId != null) {
            notificationItemService.addNotificationItem(apsUserId, notificationId);
        }
    }

    public List<Help> findHelpByLang(Lang lang) {
        return helpItemRepo.findByLang(lang).stream().map(HelpItemMapper::help).toList();
    }

}
