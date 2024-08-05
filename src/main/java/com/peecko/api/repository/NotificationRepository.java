package com.peecko.api.repository;

import com.peecko.api.domain.Notification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class NotificationRepository {
    public static final List<Notification> DATA = new ArrayList<>();

    public static final String TITLE_1 = "Weloner Marathon Luxembourg ";
    public static final String TITLE_2 = "Meet Matheo, New Health Coach, We are boarding!";
    public static final String MESSAGE_1 = "The Weloner Marathon is an annual marathon by night in Luxembourg which was held for the first time in 2006";
    public static final String MESSAGE_2 = "Matheo is a Wellness Coach Certified, Matheo is specialized in Healing from chronic health issues and Healing Nutrition";
    public static final String IMAGE_1 = "http://path/filename.jpg";
    public static final String IMAGE_2 = "http://path/filename.jpg";
    public static final String VIDEO_1 = "http://path/filename.jpg";
    public static final String VIDEO_2 = "http://path/filename.jpg";

    public static final HashMap<String, List<Notification>> NOTIFICATIONS =  new HashMap<>();

    private void initUserNotification(String username) {
        if (!NOTIFICATIONS.containsKey(username)) {
            List<Notification> news = new ArrayList<>();
            news.add(new Notification(1L, TITLE_1, MESSAGE_1, IMAGE_1, VIDEO_1, "07 Dec 2023", false));
            news.add(new Notification(2L, TITLE_2, MESSAGE_2, IMAGE_2, VIDEO_2, "05 Dec 2023", false));
            NOTIFICATIONS.put(username, news);
        }
    }

    public List<Notification> getNotifications(String username) {
        initUserNotification(username);
        return NOTIFICATIONS.get(username);
    }

    public void updateNotification(String username, Long id) {
        initUserNotification(username);
         NOTIFICATIONS.get(username)
                 .stream()
                 .filter(n -> id.equals(n.getId()))
                 .findAny()
                 .ifPresent(this::setNotificationAsViewed);
    }

    private void setNotificationAsViewed(Notification notification) {
        notification.setViewed(true);
    }

}
