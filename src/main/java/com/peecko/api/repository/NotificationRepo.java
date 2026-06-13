package com.peecko.api.repository;

import com.peecko.api.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n JOIN n.customer c WHERE c.id = :customerId AND n.starts <= :today AND n.expires > :today ORDER BY n.starts ASC")
    List<Notification> findByCustomerIdAndForToday(@Param("customerId") Long customerId, LocalDate today);

}
