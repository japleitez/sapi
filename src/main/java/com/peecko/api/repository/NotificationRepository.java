package com.peecko.api.repository;

import com.peecko.api.domain.Notification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    static {
        DATA.add(new Notification(TITLE_1, MESSAGE_1, IMAGE_1, VIDEO_1, "07 Dec 2023"));
        DATA.add(new Notification(TITLE_2, MESSAGE_2, IMAGE_2, VIDEO_2, "05 Dec 2023"));
    }

    public List<Notification> getNotifications() {
        return DATA;
    }

}
