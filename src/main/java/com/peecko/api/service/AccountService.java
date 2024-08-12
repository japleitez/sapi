package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.NotificationDTO;
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

    private final ApsUserRepo apsUserRepo;
    private final NotificationRepo notificationRepo;
    private final ApsMembershipRepo apsMembershipRepo;
    private final NotificationItemRepo notificationItemRepo;

    public AccountService(ApsUserRepo apsUserRepo, NotificationItemRepo notificationItemRepo, NotificationRepo notificationRepo, ApsMembershipRepo apsMembershipRepo) {
        this.apsUserRepo = apsUserRepo;
        this.notificationRepo = notificationRepo;
        this.apsMembershipRepo = apsMembershipRepo;
        this.notificationItemRepo = notificationItemRepo;
    }

    public List<NotificationDTO> getNotifications(String username) {
        Optional<ApsUser> optUser = apsUserRepo.findByUsername(username);
        if (optUser.isPresent()) {
            ApsUser apsUser = optUser.get();
            Optional<ApsMembership> optMembership = apsMembershipRepo.findByUsernameAndPeriod(username, Common.currentYearMonth());
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

}
