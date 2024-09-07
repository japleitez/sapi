package com.peecko.api.service;

import com.peecko.api.domain.*;
import com.peecko.api.domain.dto.NotificationDTO;
import com.peecko.api.domain.enumeration.CustomerState;
import com.peecko.api.domain.enumeration.Lang;
import com.peecko.api.repository.*;
import com.peecko.api.utils.Common;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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

    @BeforeEach
    void beforeEach() {
        viewedNotificationRepo.deleteAll();
        viewedNotificationRepo.flush();

        notificationRepo.deleteAll();
        notificationRepo.flush();

        apsMembershipRepo.deleteAll();
        apsMembershipRepo.flush();

        apsUserRepo.deleteAll();
        apsUserRepo.flush();

        customerRepo.deleteAll();
        customerRepo.flush();
    }

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
        int period = Common.currentPeriod();
        String licence = "test";

        // customer
        Customer customer = new Customer();
        customer.setCode("test");
        customer.setName("Test");
        customer.setCountry("FR");
        customer.setState(CustomerState.ACTIVE);
        customer.vatRate(0.2);
        customer.setLicense(licence);
        customerRepo.save(customer);
        customerRepo.flush();

        // user
        ApsUser apsUser = new ApsUser();
        apsUser.setUsername("test@mail.com");
        apsUser.setName("Test");
        apsUser.setLanguage(Lang.FR);
        apsUserRepo.save(apsUser);
        apsUserRepo.flush();

        // membership
        ApsMembership apsMembership = new ApsMembership();
        apsMembership.setUsername(apsUser.getUsername());
        apsMembership.setPeriod(period);
        apsMembership.setCustomerId(customer.getId());
        apsMembership.setLicense(licence);
        apsMembership.setCustomerId(customer.getId());
        apsMembershipRepo.save(apsMembership);
        apsMembershipRepo.flush();

        // notification
        Notification notification = new Notification();
        notification.setTitle("Test");
        notification.setMessage("Test");
        notification.setLanguage(Lang.FR);
        notification.setCustomer(customer);
        notification.setVideoUrl("test");
        notification.setImageUrl("test");
        notification.setStarts(LocalDate.now());
        notification.setExpires(LocalDate.now().plusDays(10));
        notificationRepo.save(notification);
        notificationRepo.flush();

        // WHEN
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