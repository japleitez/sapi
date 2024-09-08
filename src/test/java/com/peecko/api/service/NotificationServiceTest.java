package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.NotificationDTO;
import com.peecko.api.domain.enumeration.CustomerState;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.*;
import com.peecko.api.utils.Common;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ViewedNotificationRepo viewedNotificationRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Autowired
    private ApsUserRepo apsUserRepo;

    @Autowired
    private ApsMembershipRepo apsMembershipRepo;

    @Test
    void addViewedNotification() {
        //GIVEN
        Long apsUserId = 1L;
        Long notificationId = 1L;

        // WHEN
        notificationService.addViewedNotification(apsUserId, notificationId);

        // THEN
        assertTrue(viewedNotificationRepo.findByApsUserIdAndNotificationId(apsUserId, notificationId).isPresent());
    }

    @Test
    void getNotificationsForUserAndPeriod() {
        // GIVEN
        Customer customer = EntityBuilder.buildCustomer();
        customerRepo.save(customer);
        ApsUser apsUser = EntityBuilder.buildApsUser();
        apsUserRepo.save(apsUser);
        ApsMembership apsMembership = EntityBuilder.buildApsMembership(apsUser.getUsername(), customer.getId());
        apsMembershipRepo.save(apsMembership);
        Notification notification = EntityBuilder.buildNotification(customer);
        notificationRepo.save(notification);
        notificationRepo.flush();

        // WHEN
        int period = Common.currentPeriod();
        List<NotificationDTO> notViewed = notificationService.getNotificationsForUserAndPeriod(apsUser, period);
        notificationService.addViewedNotification(apsUser.getId(), notification.getId());
        List<NotificationDTO> viewed = notificationService.getNotificationsForUserAndPeriod(apsUser, period);

        // THEN
        assertNotNull(customer.getId());
        assertNotNull(apsUser.getId());
        assertNotNull(apsMembership.getId());
        assertNotNull(notification.getId());
        assertFalse(notViewed.get(0).getViewed());
        assertTrue(viewed.get(0).getViewed());

    }

}