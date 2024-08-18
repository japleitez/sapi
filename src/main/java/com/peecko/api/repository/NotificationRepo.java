package com.peecko.api.repository;

import com.peecko.api.domain.Customer;
import com.peecko.api.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    List<Notification> findByCustomerAndExpiresAfter(Customer customer, LocalDate currentDate);
}
