package com.peecko.api.repository;

import com.peecko.api.domain.NotificationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationItemRepo extends JpaRepository<NotificationItem, Long> {

    void deleteByApsUserIdAndNotificationId(Long apsUserId, Long notificationId);

    boolean existsByApsUserIdAndNotificationId(Long apsUserId, Long notificationId);

    List<NotificationItem> findByApsUserIdAndNotificationIdIn(Long apsUserId, List<Long> notificationIds);

}
