package com.peecko.api.repository;

import com.peecko.api.domain.ViewedNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewedNotificationRepo extends JpaRepository<ViewedNotification, Long> {

    Optional<ViewedNotification> findByApsUserIdAndNotificationId(Long apsUserId, Long notificationId);

    List<ViewedNotification> findByApsUserIdAndNotificationIdIn(Long apsUserId, List<Long> notificationIds);

}
